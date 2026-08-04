package com.banking.transaction_processor.controller;

import com.banking.transaction_processor.constants.Constant;
import com.banking.transaction_processor.dto.*;
import com.banking.transaction_processor.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit/{accountId}")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(@PathVariable Long accountId, @RequestBody DepositRequest request) {

        TransactionResponse response = transactionService.deposit(accountId, request.getAmount());
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message(Constant.DEPOSIT_SUCCESSFUL)
                .data(response)
                .build());
    }

    @PostMapping("/withdraw/{accountId}")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(@PathVariable Long accountId, @RequestBody WithdrawRequest request) {

        TransactionResponse response = transactionService.withdraw(accountId, request.getAmount());
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message(Constant.WITHDRAW_SUCCESSFUL)
                .data(response)
                .build());
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(@RequestBody TransferRequest transferRequest) {

        TransactionResponse response = transactionService.transfer(transferRequest);
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message(Constant.TRANSFER_SUCCESSFUL)
                .data(response)
                .build());
    }

    @GetMapping("/history/{accountId}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> history(@PathVariable Long accountId) {

        List<TransactionResponse> response = transactionService.history(accountId);
        return ResponseEntity.ok(ApiResponse.<List<TransactionResponse>>builder()
                        .success(true)
                        .message(Constant.TRANSACTION_HISTORY)
                        .data(response)
                        .build()
        );
    }
}
