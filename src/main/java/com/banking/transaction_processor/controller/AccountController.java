package com.banking.transaction_processor.controller;

import com.banking.transaction_processor.constants.Constant;
import com.banking.transaction_processor.dto.AccountResponse;
import com.banking.transaction_processor.dto.ApiResponse;
import com.banking.transaction_processor.dto.CreateAccountRequest;
import com.banking.transaction_processor.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest) {

        AccountResponse response = accountService.createAccount(createAccountRequest);
        return ResponseEntity.ok(ApiResponse.<AccountResponse>builder()
                .success(true)
                .message(Constant.ACCOUNT_CREATED)
                .data(response).build());
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<ApiResponse<AccountResponse>> getBalance(@PathVariable Long id) {

        AccountResponse response = accountService.getBalance(id);
        return ResponseEntity.ok(ApiResponse.<AccountResponse>builder()
                .success(true)
                .message(Constant.ACCOUNT_BALANCE)
                .data(response).build());
    }
}
