package com.banking.transaction_processor.service;

import com.banking.transaction_processor.dto.AccountResponse;
import com.banking.transaction_processor.dto.CreateAccountRequest;

public interface AccountService {

    public AccountResponse createAccount(CreateAccountRequest createAccountRequest);
    public AccountResponse getBalance(Long id);
}