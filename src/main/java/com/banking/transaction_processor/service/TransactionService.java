package com.banking.transaction_processor.service;

import com.banking.transaction_processor.dto.TransactionResponse;
import com.banking.transaction_processor.dto.TransferRequest;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    public TransactionResponse deposit(Long accountId, BigDecimal amount);
    public TransactionResponse withdraw(Long accountId, BigDecimal amount);
    public TransactionResponse transfer(TransferRequest transferRequest);
    public List<TransactionResponse> history(Long accountId);
}