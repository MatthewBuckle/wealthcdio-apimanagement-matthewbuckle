package com.banking.transaction_processor.service.impl;

import com.banking.transaction_processor.constants.Constant;
import com.banking.transaction_processor.dto.AccountResponse;
import com.banking.transaction_processor.dto.CreateAccountRequest;
import com.banking.transaction_processor.entity.Account;
import com.banking.transaction_processor.exception.AccountNotFoundException;
import com.banking.transaction_processor.repository.AccountRepository;
import com.banking.transaction_processor.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse createAccount(CreateAccountRequest createAccountRequest) {
        Account account = Account.builder().
                accountHolderName(createAccountRequest.getAccountHolderName()).
                email(createAccountRequest.getEmail()).
                balance(BigDecimal.valueOf(0.00)).build();

        accountRepository.save(account);
        return AccountResponse.builder()
                .accountHolderName(account.getAccountHolderName())
                .email(account.getEmail()).build();
    }

    public AccountResponse getBalance(Long id) {
        Account account = getAccount(id);
        return AccountResponse.builder()
                .accountHolderName(account.getAccountHolderName())
                .balance(account.getBalance()).build();

    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(Constant.ACCOUNT_NOT_FOUND));
    }
}