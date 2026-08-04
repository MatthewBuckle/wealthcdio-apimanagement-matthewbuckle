package com.banking.transaction_processor.service;

import com.banking.transaction_processor.constants.Constant;
import com.banking.transaction_processor.dto.TransactionResponse;
import com.banking.transaction_processor.dto.TransferRequest;
import com.banking.transaction_processor.entity.Account;
import com.banking.transaction_processor.entity.Transaction;
import com.banking.transaction_processor.exception.AccountNotFoundException;
import com.banking.transaction_processor.exception.InsufficientFundsException;
import com.banking.transaction_processor.repository.AccountRepository;
import com.banking.transaction_processor.repository.TransactionRepository;
import com.banking.transaction_processor.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account account;

    @BeforeEach
    void setup() {
        account = Account.builder()
                .id(1L)
                .accountHolderName("Matthew Buckle")
                .email("matthewbuckle@gmail.com")
                .balance(BigDecimal.valueOf(1000))
                .build();
    }

    @Test
    void deposit_ShouldDepositSuccessfully() {

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Transaction transaction = Transaction.builder().referenceNumber("REF001").amount(BigDecimal.valueOf(500)).transactionType(Constant.DEPOSIT).timestamp(LocalDateTime.now()).build();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        TransactionResponse response = transactionService.deposit(1L, BigDecimal.valueOf(500));
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(1500), response.getBalance());
        verify(accountRepository).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void deposit_ShouldThrowException_WhenAmountInvalid() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transactionService.deposit(1L, BigDecimal.ZERO));
        assertEquals(Constant.INVALID_AMOUNT, exception.getMessage());
    }

    @Test
    void withdraw_ShouldWithdrawSuccessfully() {

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Transaction transaction = Transaction.builder().referenceNumber("REF002").transactionType(Constant.WITHDRAW).amount(BigDecimal.valueOf(300)).timestamp(LocalDateTime.now()).build();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        TransactionResponse response = transactionService.withdraw(1L, BigDecimal.valueOf(300));
        assertEquals(BigDecimal.valueOf(700), response.getBalance());
    }

    @Test
    void withdraw_ShouldThrowException_WhenInsufficientFunds() {

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        assertThrows(InsufficientFundsException.class, () -> transactionService.withdraw(1L, BigDecimal.valueOf(2000)));
    }

    @Test
    void transfer_ShouldTransferSuccessfully() {

        Account toAccount = Account.builder().id(2L).accountHolderName("Jane").balance(BigDecimal.valueOf(500)).build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Transaction transaction = Transaction.builder().referenceNumber("REF003").transactionType(Constant.TRANSFER_OUT).amount(BigDecimal.valueOf(200)).timestamp(LocalDateTime.now()).build();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        TransactionResponse response = transactionService.transfer(TransferRequest.builder().fromAccountId(1L).toAccountId(2L).amount(BigDecimal.valueOf(200)).build());
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(800), response.getBalance());
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void transfer_ShouldThrowException_WhenSameAccount() {

        TransferRequest request = TransferRequest.builder().fromAccountId(1L).toAccountId(1L).amount(BigDecimal.valueOf(100)).build();
        assertThrows(IllegalArgumentException.class, () -> transactionService.transfer(request));
    }

    @Test
    void transfer_ShouldThrowException_WhenInsufficientFunds() {

        Account toAccount = Account.builder().id(2L).balance(BigDecimal.valueOf(100)).build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));
        TransferRequest request = TransferRequest.builder().fromAccountId(1L).toAccountId(2L).amount(BigDecimal.valueOf(5000)).build();
        assertThrows(InsufficientFundsException.class, () -> transactionService.transfer(request));
    }

    @Test
    void history_ShouldReturnTransactions() {

        Transaction transaction = Transaction.builder().referenceNumber("REF004").transactionType(Constant.DEPOSIT).amount(BigDecimal.valueOf(400)).timestamp(LocalDateTime.now()).build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccount_IdOrderByTimestampDesc(1L)).thenReturn(List.of(transaction));
        List<TransactionResponse> response = transactionService.history(1L);
        assertEquals(1, response.size());
        assertEquals("REF004", response.get(0).getTransactionReferenceNumber());
    }

    @Test
    void statement_ShouldReturnTransactionsWithinDateRange() {

        LocalDate fromDate = LocalDate.now().minusDays(10);
        LocalDate toDate = LocalDate.now();
        Transaction transaction = Transaction.builder().referenceNumber("REF005").transactionType(Constant.DEPOSIT).amount(BigDecimal.valueOf(100)).timestamp(LocalDateTime.now()).build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccount_IdAndTimestampBetweenOrderByTimestampDesc(anyLong(), any(), any())).thenReturn(List.of(transaction));
        List<TransactionResponse> response = transactionService.statement(1L, fromDate, toDate);
        assertEquals(1, response.size());
    }

    @Test
    void statement_ShouldThrowException_WhenDateRangeInvalid() {

        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().minusDays(1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transactionService.statement(1L, fromDate, toDate));
        assertEquals(Constant.INVALID_DATE, exception.getMessage());
    }

    @Test
    void statement_ShouldReturnAllTransactions_WhenDatesAreNull() {

        Transaction transaction = Transaction.builder().referenceNumber("REF006").transactionType(Constant.DEPOSIT).amount(BigDecimal.valueOf(250)).timestamp(LocalDateTime.now()).build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccount_IdOrderByTimestampDesc(1L)).thenReturn(List.of(transaction));
        List<TransactionResponse> response = transactionService.statement(1L, null, null);
        assertEquals(1, response.size());
        verify(transactionRepository).findByAccount_IdOrderByTimestampDesc(1L);
    }

    @Test
    void deposit_ShouldThrowAccountNotFoundException() {

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> transactionService.deposit(1L, BigDecimal.valueOf(100)));
    }

    @Test
    void withdraw_ShouldThrowAccountNotFoundException() {

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> transactionService.withdraw(1L, BigDecimal.valueOf(100)));
    }

    @Test
    void withdraw_ShouldThrowInvalidAmountException_WhenZeroAmount() {

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> transactionService.withdraw(1L, BigDecimal.ZERO));
        assertEquals(Constant.INVALID_AMOUNT, ex.getMessage());
    }

    @Test
    void withdraw_ShouldThrowInvalidAmountException_WhenNegativeAmount() {

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> transactionService.withdraw(1L, BigDecimal.valueOf(-100)));
        assertEquals(Constant.INVALID_AMOUNT, ex.getMessage());
    }

    @Test
    void transfer_ShouldThrowInvalidAmountException_WhenAmountNull() {

        TransferRequest request = TransferRequest.builder().fromAccountId(1L).toAccountId(2L).amount(null).build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> transactionService.transfer(request));
        assertEquals(Constant.INVALID_AMOUNT, ex.getMessage());
    }

    @Test
    void transfer_ShouldThrowInvalidAmountException_WhenAmountNegative() {

        TransferRequest request = TransferRequest.builder().fromAccountId(1L).toAccountId(2L).amount(BigDecimal.valueOf(-100)).build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> transactionService.transfer(request));
        assertEquals(Constant.INVALID_AMOUNT, ex.getMessage());
    }

    @Test
    void transfer_ShouldThrowSourceAccountNotFoundException() {

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        TransferRequest request = TransferRequest.builder().fromAccountId(1L).toAccountId(2L).amount(BigDecimal.valueOf(100)).build();
        assertThrows(AccountNotFoundException.class, () -> transactionService.transfer(request));
    }

    @Test
    void transfer_ShouldThrowDestinationAccountNotFoundException() {

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());
        TransferRequest request = TransferRequest.builder().fromAccountId(1L).toAccountId(2L).amount(BigDecimal.valueOf(100)).build();
        assertThrows(AccountNotFoundException.class, () -> transactionService.transfer(request));
    }

    @Test
    void statement_ShouldThrowAccountNotFoundException() {

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> transactionService.statement(1L, null, null));
    }

    @Test
    void history_ShouldThrowAccountNotFoundException() {

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> transactionService.history(1L));
    }

    @Test
    void history_ShouldMapTransactionToResponse() {

        Transaction transaction = Transaction.builder().referenceNumber("REF100").transactionType(Constant.DEPOSIT).amount(BigDecimal.valueOf(500)).timestamp(LocalDateTime.now()).build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccount_IdOrderByTimestampDesc(1L)).thenReturn(List.of(transaction));
        List<TransactionResponse> result = transactionService.history(1L);
        assertEquals(1, result.size());
        TransactionResponse response = result.get(0);
        assertEquals("REF100", response.getTransactionReferenceNumber());
        assertEquals(Constant.DEPOSIT, response.getTransactionType());
        assertEquals(BigDecimal.valueOf(500), response.getTransactionAmount());
        assertNotNull(response.getTimestamp());
    }
}
