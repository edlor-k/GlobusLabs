package ru.globus.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.globus.dto.BankAccountRequestDto;
import ru.globus.dto.BankAccountResponseDto;
import ru.globus.dto.TransferRequestDto;
import ru.globus.model.enums.CurrencyCode;
import ru.globus.service.BankAccountService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BankAccountControllerTest {

    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private BankAccountController controller;

    @Test
    void getAll_shouldReturnPageOfBankAccountResponseDto() {
        Pageable pageable = PageRequest.of(0, 10);
        BankAccountResponseDto dto = new BankAccountResponseDto(UUID.randomUUID(), UUID.randomUUID(), CurrencyCode.USD, "123", BigDecimal.TEN, true, LocalDateTime.now());
        Page<BankAccountResponseDto> page = new PageImpl<>(List.of(dto));
        when(bankAccountService.getAll(any(Pageable.class))).thenReturn(page);

        Page<BankAccountResponseDto> result = controller.getAll(pageable);

        assertThat(result).isEqualTo(page);
        verify(bankAccountService).getAll(pageable);
    }

    @Test
    void createAccount_shouldReturnBankAccountResponseDto() {
        BankAccountRequestDto request = new BankAccountRequestDto(UUID.randomUUID(), CurrencyCode.USD, "123", BigDecimal.TEN);
        BankAccountResponseDto response = new BankAccountResponseDto(UUID.randomUUID(), request.userId(), request.currencyCode(), request.accountNumber(), request.balance(), true, LocalDateTime.now());
        when(bankAccountService.create(any(BankAccountRequestDto.class))).thenReturn(response);

        BankAccountResponseDto result = controller.createAccount(request);

        assertThat(result).isEqualTo(response);
        verify(bankAccountService).create(request);
    }

    @Test
    void getAccountById_shouldReturnBankAccountResponseDto() {
        UUID id = UUID.randomUUID();
        BankAccountResponseDto response = new BankAccountResponseDto(id, UUID.randomUUID(), CurrencyCode.USD, "123", BigDecimal.TEN, true, LocalDateTime.now());
        when(bankAccountService.getById(id)).thenReturn(response);

        BankAccountResponseDto result = controller.getAccountById(id);

        assertThat(result).isEqualTo(response);
        verify(bankAccountService).getById(id);
    }

    @Test
    void updateAccount_shouldReturnBankAccountResponseDto() {
        UUID id = UUID.randomUUID();
        BankAccountRequestDto request = new BankAccountRequestDto(UUID.randomUUID(), CurrencyCode.USD, "123", BigDecimal.TEN);
        BankAccountResponseDto response = new BankAccountResponseDto(id, request.userId(), request.currencyCode(), request.accountNumber(), request.balance(), true, LocalDateTime.now());
        when(bankAccountService.update(any(UUID.class), any(BankAccountRequestDto.class))).thenReturn(response);

        BankAccountResponseDto result = controller.updateAccount(id, request);

        assertThat(result).isEqualTo(response);
        verify(bankAccountService).update(id, request);
    }

    @Test
    void deleteAccount_shouldCallServiceDelete() {
        UUID id = UUID.randomUUID();
        controller.deleteAccount(id);
        verify(bankAccountService).delete(id);
    }

    @Test
    void transferFunds_shouldCallServiceTransfer() {
        TransferRequestDto request = new TransferRequestDto(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN);
        controller.transferFunds(request);
        verify(bankAccountService).transfer(request);
    }
}
