package com.banking.transaction_processor.service.impl;

import com.banking.transaction_processor.constants.Constant;
import com.banking.transaction_processor.dto.TransactionResponse;
import com.banking.transaction_processor.dto.TransferRequest;
import com.banking.transaction_processor.entity.Account;
import com.banking.transaction_processor.entity.Transaction;
import com.banking.transaction_processor.exception.AccountNotFoundException;
import com.banking.transaction_processor.exception.InsufficientFundsException;
import com.banking.transaction_processor.repository.AccountRepository;
import com.banking.transaction_processor.repository.TransactionRepository;
import com.banking.transaction_processor.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    public TransactionResponse deposit(Long accountId, BigDecimal amount) {
        validateAmount(amount);
        Account account = getAccount(accountId);
        account.setBalance(account.getBalance().add(amount));
        Account accountAfterTransaction = accountRepository.save(account);
        Transaction tx = recordTransaction(account, Constant.DEPOSIT, amount);
        return TransactionResponse.builder()
                .transactionReferenceNumber(tx.getReferenceNumber())
                .transactionAmount(amount)
                .transactionType(Constant.DEPOSIT)
                .balance(accountAfterTransaction.getBalance())
                .timestamp(tx.getTimestamp()).build();
    }

    public TransactionResponse withdraw(Long accountId, BigDecimal amount) {
        validateAmount(amount);
        Account account = getAccount(accountId);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(Constant.INSUFFICIENT_FUNDS);
        }
        account.setBalance(account.getBalance().subtract(amount));
        Account accountAfterTransaction = accountRepository.save(account);
        Transaction tx = recordTransaction(account, Constant.WITHDRAW, amount);
        return TransactionResponse.builder()
                .transactionReferenceNumber(tx.getReferenceNumber())
                .transactionAmount(amount)
                .transactionType(Constant.WITHDRAW)
                .balance(accountAfterTransaction.getBalance())
                .timestamp(tx.getTimestamp()).build();
    }

    public TransactionResponse transfer(TransferRequest transferRequest) {

        Long fromId = transferRequest.getFromAccountId();
        Long toId = transferRequest.getToAccountId();
        BigDecimal amount = transferRequest.getAmount();
        validateAmount(amount);

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }

        Account from = getAccount(fromId);
        Account to = getAccount(toId);

        if (from.getBalance().compareTo(amount) < 0) {

            throw new InsufficientFundsException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        Account accountAfterTransaction = accountRepository.save(from);
        accountRepository.save(to);

        Transaction tx = recordTransaction(from, Constant.TRANSFER_OUT, amount);
        recordTransaction(to, Constant.TRANSFER_IN, amount);
        return TransactionResponse.builder()
                .transactionReferenceNumber(tx.getReferenceNumber())
                .transactionAmount(amount)
                .transactionType(Constant.TRANSFER_OUT)
                .balance(accountAfterTransaction.getBalance())
                .timestamp(tx.getTimestamp()).build();
    }

    public List<TransactionResponse> history(Long accountId) {
        getAccount(accountId);
        List<Transaction> transactions =
                transactionRepository.findByAccount_IdOrderByTimestampDesc(accountId);
        return transactions.stream()
                .map(this::toResponse)
                .toList();
    }

    private Transaction recordTransaction(Account account, String type, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(type)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .referenceNumber(UUID.randomUUID().toString()).build();
        return transactionRepository.save(transaction);
    }

    private Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(Constant.ACCOUNT_NOT_FOUND));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(Constant.INVALID_AMOUNT);
        }
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionReferenceNumber(transaction.getReferenceNumber())
                .transactionType(transaction.getTransactionType())
                .transactionAmount(transaction.getAmount())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}