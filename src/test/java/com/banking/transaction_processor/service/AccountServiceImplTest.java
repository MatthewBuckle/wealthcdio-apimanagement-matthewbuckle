package com.banking.transaction_processor.service;

import com.banking.transaction_processor.constants.Constant;
import com.banking.transaction_processor.dto.AccountResponse;
import com.banking.transaction_processor.dto.CreateAccountRequest;
import com.banking.transaction_processor.entity.Account;
import com.banking.transaction_processor.exception.AccountNotFoundException;
import com.banking.transaction_processor.repository.AccountRepository;
import com.banking.transaction_processor.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void createAccount_ShouldCreateAccountSuccessfully() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountHolderName("Matthew Buckle");
        request.setEmail("matthewbuckle@gmail.com");

        Account savedAccount = Account.builder()
                .id(1L)
                .accountHolderName("Matthew Buckle")
                .email("matthewbuckle@gmail.com")
                .balance(BigDecimal.ZERO)
                .build();

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        AccountResponse response = accountService.createAccount(request);
        assertNotNull(response);
        assertEquals("Matthew Buckle", response.getAccountHolderName());
        assertEquals("matthewbuckle@gmail.com", response.getEmail());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void getBalance_ShouldReturnAccountBalance() {
        Long accountId = 1L;

        Account account = Account.builder()
                .id(accountId)
                .accountHolderName("Matthew Buckle")
                .email("matthewbuckle@gmail.com")
                .balance(BigDecimal.valueOf(5000))
                .build();

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));
        AccountResponse response = accountService.getBalance(accountId);

        assertNotNull(response);
        assertEquals("Matthew Buckle", response.getAccountHolderName());
        assertEquals(BigDecimal.valueOf(5000), response.getBalance());
        verify(accountRepository).findById(accountId);
    }

    @Test
    void getAccount_ShouldReturnAccount_WhenAccountExists() {
        Long accountId = 1L;
        Account account = Account.builder()
                .id(accountId)
                .accountHolderName("Matthew Buckle")
                .email("matthewbuckle@gmail.com")
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        Account result = accountService.getAccount(accountId);

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        assertEquals("Matthew Buckle", result.getAccountHolderName());
        verify(accountRepository).findById(accountId);
    }

    @Test
    void getAccount_ShouldThrowException_WhenAccountNotFound() {
        Long accountId = 99L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class, () -> accountService.getAccount(accountId));

        assertEquals(Constant.ACCOUNT_NOT_FOUND, exception.getMessage());
        verify(accountRepository).findById(accountId);
    }
}