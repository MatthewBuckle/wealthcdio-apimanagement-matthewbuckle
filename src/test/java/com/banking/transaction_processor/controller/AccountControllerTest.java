package com.banking.transaction_processor.controller;

import com.banking.transaction_processor.constants.Constant;
import com.banking.transaction_processor.dto.AccountResponse;
import com.banking.transaction_processor.dto.ApiResponse;
import com.banking.transaction_processor.dto.CreateAccountRequest;
import com.banking.transaction_processor.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    void createAccount_ShouldReturnSuccessResponse() {

        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountHolderName("Matthew Buckle");
        request.setEmail("matthewbuckle@gmail.com");
        AccountResponse serviceResponse = AccountResponse.builder()
                .accountHolderName("Matthew Buckle")
                .email("matthewbuckle@gmail.com")
                .build();

        when(accountService.createAccount(request)).thenReturn(serviceResponse);
        ResponseEntity<ApiResponse<AccountResponse>> response = accountController.createAccount(request);
        ApiResponse<AccountResponse> body = response.getBody();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals(Constant.ACCOUNT_CREATED, body.getMessage());
        assertEquals("Matthew Buckle", body.getData().getAccountHolderName());
        assertEquals("matthewbuckle@gmail.com", body.getData().getEmail());
        verify(accountService, times(1)).createAccount(request);
    }

    @Test
    void getBalance_ShouldReturnBalanceSuccessfully() {

        Long accountId = 1L;
        AccountResponse serviceResponse = AccountResponse.builder()
                .accountHolderName("Matthew Buckle")
                .balance(BigDecimal.valueOf(5000))
                .build();

        when(accountService.getBalance(accountId)).thenReturn(serviceResponse);
        ResponseEntity<ApiResponse<AccountResponse>> response =
                accountController.getBalance(accountId);
        ApiResponse<AccountResponse> body = response.getBody();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals(Constant.ACCOUNT_BALANCE, body.getMessage());
        assertEquals("Matthew Buckle", body.getData().getAccountHolderName());
        assertEquals(BigDecimal.valueOf(5000), body.getData().getBalance());
        verify(accountService, times(1)).getBalance(accountId);
    }
}
