package ru.globus.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.globus.aop.LogMethod;
import ru.globus.dto.BankAccountEventDto;
import ru.globus.dto.BankAccountRequestDto;
import ru.globus.dto.BankAccountResponseDto;
import ru.globus.dto.TransferRequestDto;
import ru.globus.exception.BankAccountNotFoundException;
import ru.globus.exception.UserNotFoundException;
import ru.globus.kafka.BankAccountEventProducer;
import ru.globus.mapper.BankAccountMapper;
import ru.globus.model.entity.BankAccount;
import ru.globus.repository.BankAccountRepository;
import ru.globus.repository.UserRepository;
import ru.globus.service.BankAccountService;
import ru.globus.service.CurrencyRateService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Имплементация BankAccountService.
 * Содержит бизнес-логику по управлению банковскими счетами пользователей.
 *
 * @author Vladlen Korablev
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final BankAccountMapper bankAccountMapper;
    private final CurrencyRateService currencyRateService;
    private final BankAccountEventProducer eventProducer;

    /**
     * Создаёт новый банковский счёт.
     */
    @Override
    @LogMethod("account-create")
    public BankAccountResponseDto create(BankAccountRequestDto dto) {
        var user = userRepository.findById(dto.userId())
            .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + dto.userId()));

        if (bankAccountRepository.existsByAccountNumber(dto.accountNumber())) {
            log.error("Счёт с номером {} уже существует", dto.accountNumber());
            throw new IllegalArgumentException("Счёт с номером " + dto.accountNumber() + " уже существует");
        }

        var account = bankAccountMapper.toEntity(dto, user);
        var saved = bankAccountRepository.save(account);

        sendBankAccountEvent(saved, BankAccountEventDto.EventType.ACCOUNT_CREATED,
                "Создан новый банковский счёт");

        return bankAccountMapper.toResponseDto(saved);
    }

    /**
     * Возвращает все счета с пагинацией.
     */
    @Override
    @LogMethod("account-get-all")
    @Transactional(readOnly = true)
    public Page<BankAccountResponseDto> getAll(Pageable pageable) {
        return bankAccountRepository.findAll(pageable)
            .map(bankAccountMapper::toResponseDto);
    }

    /**
     * Возвращает счёт по идентификатору.
     */
    @Override
    @LogMethod("account-get-id")
    @Transactional(readOnly = true)
    public BankAccountResponseDto getById(UUID id) {
        var account = bankAccountRepository.findById(id)
            .orElseThrow(() -> new BankAccountNotFoundException("Счёт не найден: " + id));
        return bankAccountMapper.toResponseDto(account);
    }

    /**
     * Обновляет данные счёта.
     */
    @Override
    @LogMethod("account-update")
    public BankAccountResponseDto update(UUID id, BankAccountRequestDto dto) {
        var account = bankAccountRepository.findById(id)
            .orElseThrow(() -> new BankAccountNotFoundException("Счёт не найден: " + id));

        if (dto.balance() != null) {
            account.setBalance(dto.balance());
        }

        var updated = bankAccountRepository.save(account);

        sendBankAccountEvent(updated, BankAccountEventDto.EventType.ACCOUNT_UPDATED,
                "Обновлён банковский счёт");

        return bankAccountMapper.toResponseDto(updated);
    }

    /**
     * Удаляет счёт по ID (идемпотентно).
     */
    @Override
    @LogMethod("account-delete")
    public void delete(UUID id) {
        if (bankAccountRepository.existsById(id)) {
            var account = bankAccountRepository.findById(id)
                    .orElseThrow(() -> new BankAccountNotFoundException("Счёт не найден: " + id));

            sendBankAccountEvent(account, BankAccountEventDto.EventType.ACCOUNT_DELETED,
                    "Банковский счёт удалён");

            bankAccountRepository.deleteById(id);
            log.info("Счёт {} успешно удалён", id);
        } else {
            log.warn("Попытка удалить несуществующий счёт {}", id);
        }
    }

    /**
     * Переводит средства между счетами пользователя с конвертацией валют по текущему курсу.
     */
    @Override
    @LogMethod("account-transfer")
    @Transactional
    public void transfer(TransferRequestDto dto) {
        var fromAccount = bankAccountRepository.findById(dto.fromAccountId())
                .orElseThrow(() -> new BankAccountNotFoundException("Счёт отправителя не найден: " + dto.fromAccountId()));
        var toAccount = bankAccountRepository.findById(dto.toAccountId())
                .orElseThrow(() -> new BankAccountNotFoundException("Счёт получателя не найден: " + dto.toAccountId()));

        if (fromAccount.getBalance().compareTo(dto.amount()) < 0) {
            throw new IllegalArgumentException("Недостаточно средств на счёте отправителя");
        }

        if (!fromAccount.getUser().getId().equals(toAccount.getUser().getId())) {
            throw new IllegalArgumentException("Перевод возможен только между счетами одного пользователя");
        }

        if (dto.fromAccountId().equals(dto.toAccountId())) {
            throw new IllegalArgumentException("Нельзя выполнить перевод на тот же самый счёт");
        }

        if (!fromAccount.getActive() || !toAccount.getActive()) {
            throw new IllegalArgumentException("Один из счетов неактивен");
        }

        LocalDate today = LocalDate.now();

        BigDecimal crossRate = currencyRateService.getConversionRate(
                fromAccount.getCurrencyCode(),
                toAccount.getCurrencyCode(),
                today
        );

        BigDecimal convertedAmount = dto.amount()
                .multiply(crossRate)
                .setScale(2, RoundingMode.HALF_UP);

        fromAccount.withdraw(dto.amount());
        toAccount.deposit(convertedAmount);

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);

        sendBankAccountEvent(fromAccount, BankAccountEventDto.EventType.TRANSFER_COMPLETED,
                String.format("Перевод %s %s на счёт %s", dto.amount(), fromAccount.getCurrencyCode(), dto.toAccountId()));
        sendBankAccountEvent(toAccount, BankAccountEventDto.EventType.BALANCE_CHANGED,
                String.format("Получен перевод %s %s", convertedAmount, toAccount.getCurrencyCode()));

        log.info(
                "Перевод {} {} (курс {}) со счёта {} на {} выполнен: {} {}",
                dto.amount(), fromAccount.getCurrencyCode(), crossRate,
                dto.fromAccountId(), dto.toAccountId(),
                convertedAmount, toAccount.getCurrencyCode()
        );
    }

    /**
     * Вспомогательный метод для отправки событий в Kafka.
     */
    private void sendBankAccountEvent(BankAccount account, BankAccountEventDto.EventType eventType, String message) {
        try {
            BankAccountEventDto event = BankAccountEventDto.builder()
                    .eventType(eventType)
                    .accountId(account.getId())
                    .userId(account.getUser().getId())
                    .balance(account.getBalance())
                    .currency(account.getCurrencyCode())
                    .timestamp(LocalDateTime.now())
                    .message(message)
                    .build();

            eventProducer.sendEvent(event);
        } catch (Exception e) {
            log.error("Ошибка при отправке события в Kafka: {}", e.getMessage(), e);
        }
    }

}
