package ru.globus.service.impl;

import org.junit.jupiter.api.BeforeEach;
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
import ru.globus.exception.BankAccountNotFoundException;
import ru.globus.exception.UserNotFoundException;
import ru.globus.mapper.BankAccountMapper;
import ru.globus.model.entity.BankAccount;
import ru.globus.model.entity.User;
import ru.globus.model.enums.CurrencyCode;
import ru.globus.repository.BankAccountRepository;
import ru.globus.repository.UserRepository;
import ru.globus.service.CurrencyRateService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountMapper bankAccountMapper;

    @Mock
    private CurrencyRateService currencyRateService;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    private UUID userId;
    private UUID accountId;
    private UUID fromAccountId;
    private UUID toAccountId;
    private User user;
    private BankAccount bankAccount;
    private BankAccountRequestDto requestDto;
    private BankAccountResponseDto responseDto;
    private TransferRequestDto transferDto;
    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        fromAccountId = UUID.randomUUID();
        toAccountId = UUID.randomUUID();
        createdAt = LocalDateTime.now();

        user = new User();
        user.setId(userId);

        bankAccount = new BankAccount();
        bankAccount.setId(accountId);
        bankAccount.setUser(user);
        bankAccount.setActive(true);
        bankAccount.setBalance(BigDecimal.valueOf(1000));
        bankAccount.setCurrencyCode(CurrencyCode.RUB);

        requestDto = new BankAccountRequestDto(userId, CurrencyCode.RUB, "1234567890", BigDecimal.valueOf(500));
        responseDto = new BankAccountResponseDto(accountId, userId, CurrencyCode.RUB, "1234567890", BigDecimal.valueOf(500), true, createdAt);

        transferDto = new TransferRequestDto(fromAccountId, toAccountId, BigDecimal.valueOf(100));
    }

    @Test
    void create_ShouldCreateAccount_WhenValidData() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bankAccountRepository.existsByAccountNumber(requestDto.accountNumber())).thenReturn(false);
        when(bankAccountMapper.toEntity(requestDto, user)).thenReturn(bankAccount);
        when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);
        when(bankAccountMapper.toResponseDto(bankAccount)).thenReturn(responseDto);

        BankAccountResponseDto result = bankAccountService.create(requestDto);

        assertEquals(responseDto, result);
        verify(userRepository).findById(userId);
        verify(bankAccountRepository).existsByAccountNumber(requestDto.accountNumber());
        verify(bankAccountRepository).save(bankAccount);
    }

    @Test
    void create_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bankAccountService.create(requestDto));
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenAccountNumberExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bankAccountRepository.existsByAccountNumber(requestDto.accountNumber())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> bankAccountService.create(requestDto));
    }

    @Test
    void getAll_ShouldReturnPagedAccounts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BankAccount> page = new PageImpl<>(List.of(bankAccount));
        when(bankAccountRepository.findAll(pageable)).thenReturn(page);
        when(bankAccountMapper.toResponseDto(bankAccount)).thenReturn(responseDto);

        Page<BankAccountResponseDto> result = bankAccountService.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(bankAccountRepository).findAll(pageable);
    }

    @Test
    void getById_ShouldReturnAccount_WhenExists() {
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(bankAccount));
        when(bankAccountMapper.toResponseDto(bankAccount)).thenReturn(responseDto);

        BankAccountResponseDto result = bankAccountService.getById(accountId);

        assertEquals(responseDto, result);
        verify(bankAccountRepository).findById(accountId);
    }

    @Test
    void getById_ShouldThrowBankAccountNotFoundException_WhenNotExists() {
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.getById(accountId));
    }

    @Test
    void update_ShouldUpdateBalance_WhenBalanceProvided() {
        BankAccountRequestDto updateDto = new BankAccountRequestDto(null, null, null, BigDecimal.valueOf(1500));
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);
        when(bankAccountMapper.toResponseDto(bankAccount)).thenReturn(responseDto);

        BankAccountResponseDto result = bankAccountService.update(accountId, updateDto);

        assertEquals(BigDecimal.valueOf(1500), bankAccount.getBalance());
        assertEquals(responseDto, result);
        verify(bankAccountRepository).save(bankAccount);
    }

    @Test
    void update_ShouldThrowBankAccountNotFoundException_WhenNotExists() {
        BankAccountRequestDto updateDto = new BankAccountRequestDto(null, null, null, BigDecimal.valueOf(1500));
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.update(accountId, updateDto));
    }

    @Test
    void delete_ShouldDeleteAccount_WhenExists() {
        when(bankAccountRepository.existsById(accountId)).thenReturn(true);

        bankAccountService.delete(accountId);

        verify(bankAccountRepository).deleteById(accountId);
    }

    @Test
    void delete_ShouldNotDelete_WhenNotExists() {
        when(bankAccountRepository.existsById(accountId)).thenReturn(false);

        bankAccountService.delete(accountId);

        verify(bankAccountRepository, never()).deleteById(accountId);
    }

    @Test
    void transfer_ShouldThrowBankAccountNotFoundException_WhenFromAccountNotFound() {
        when(bankAccountRepository.findById(fromAccountId)).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.transfer(transferDto));
    }

    @Test
    void transfer_ShouldThrowBankAccountNotFoundException_WhenToAccountNotFound() {
        when(bankAccountRepository.findById(fromAccountId)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.findById(toAccountId)).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.transfer(transferDto));
    }

    @Test
    void transfer_ShouldThrowIllegalArgumentException_WhenInsufficientFunds() {
        transferDto = new TransferRequestDto(fromAccountId, toAccountId, BigDecimal.valueOf(2000));

        when(bankAccountRepository.findById(fromAccountId)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.findById(toAccountId)).thenReturn(Optional.of(bankAccount));

        assertThrows(IllegalArgumentException.class, () -> bankAccountService.transfer(transferDto));
    }

    @Test
    void transfer_ShouldThrowIllegalArgumentException_WhenDifferentUsers() {
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        BankAccount toAccount = new BankAccount();
        toAccount.setUser(anotherUser);

        when(bankAccountRepository.findById(fromAccountId)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));

        assertThrows(IllegalArgumentException.class, () -> bankAccountService.transfer(transferDto));
    }
}
