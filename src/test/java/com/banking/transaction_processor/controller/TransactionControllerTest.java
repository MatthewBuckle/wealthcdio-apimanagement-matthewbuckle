package com.banking.transaction_processor.controller;

import com.banking.transaction_processor.constants.Constant;
import com.banking.transaction_processor.dto.*;
import com.banking.transaction_processor.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    void deposit_ShouldReturnSuccessResponse() {

        Long accountId = 1L;
        DepositRequest request = new DepositRequest();
        request.setAmount(BigDecimal.valueOf(500));

        TransactionResponse serviceResponse = TransactionResponse.builder()
                .transactionReferenceNumber("REF001")
                .transactionAmount(BigDecimal.valueOf(500))
                .transactionType(Constant.DEPOSIT)
                .balance(BigDecimal.valueOf(1500))
                .timestamp(LocalDateTime.now())
                .build();

        when(transactionService.deposit(accountId, BigDecimal.valueOf(500))).thenReturn(serviceResponse);
        ResponseEntity<ApiResponse<TransactionResponse>> response = transactionController.deposit(accountId, request);
        ApiResponse<TransactionResponse> body = response.getBody();
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals(Constant.DEPOSIT_SUCCESSFUL, body.getMessage());
        assertEquals("REF001", body.getData().getTransactionReferenceNumber());
        verify(transactionService).deposit(accountId, BigDecimal.valueOf(500));
    }

    @Test
    void withdraw_ShouldReturnSuccessResponse() {

        Long accountId = 1L;
        WithdrawRequest request = new WithdrawRequest();
        request.setAmount(BigDecimal.valueOf(200));

        TransactionResponse serviceResponse = TransactionResponse.builder()
                .transactionReferenceNumber("REF002")
                .transactionAmount(BigDecimal.valueOf(200))
                .transactionType(Constant.WITHDRAW)
                .balance(BigDecimal.valueOf(800))
                .timestamp(LocalDateTime.now())
                .build();

        when(transactionService.withdraw(accountId, BigDecimal.valueOf(200))).thenReturn(serviceResponse);
        ResponseEntity<ApiResponse<TransactionResponse>> response = transactionController.withdraw(accountId, request);
        ApiResponse<TransactionResponse> body = response.getBody();
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals(Constant.WITHDRAW_SUCCESSFUL, body.getMessage());
        assertEquals(Constant.WITHDRAW, body.getData().getTransactionType());
        verify(transactionService).withdraw(accountId, BigDecimal.valueOf(200));
    }

    @Test
    void transfer_ShouldReturnSuccessResponse() {

        TransferRequest request = TransferRequest.builder()
                .fromAccountId(1L)
                .toAccountId(2L)
                .amount(BigDecimal.valueOf(300))
                .build();
        TransactionResponse serviceResponse = TransactionResponse.builder()
                .transactionReferenceNumber("REF003")
                .transactionAmount(BigDecimal.valueOf(300))
                .transactionType(Constant.TRANSFER_OUT)
                .balance(BigDecimal.valueOf(700))
                .timestamp(LocalDateTime.now())
                .build();

        when(transactionService.transfer(request)).thenReturn(serviceResponse);
        ResponseEntity<ApiResponse<TransactionResponse>> response = transactionController.transfer(request);
        ApiResponse<TransactionResponse> body = response.getBody();
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals(Constant.TRANSFER_SUCCESSFUL, body.getMessage());
        assertEquals(Constant.TRANSFER_OUT, body.getData().getTransactionType());
        verify(transactionService).transfer(request);
    }

    @Test
    void history_ShouldReturnTransactionHistory() {

        Long accountId = 1L;

        TransactionResponse transactionResponse =
                TransactionResponse.builder()
                        .transactionReferenceNumber("REF004")
                        .transactionType(Constant.DEPOSIT)
                        .transactionAmount(BigDecimal.valueOf(100))
                        .timestamp(LocalDateTime.now())
                        .build();

        List<TransactionResponse> serviceResponse = List.of(transactionResponse);

        when(transactionService.history(accountId)).thenReturn(serviceResponse);
        ResponseEntity<ApiResponse<List<TransactionResponse>>> response = transactionController.history(accountId);
        ApiResponse<List<TransactionResponse>> body = response.getBody();
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals(Constant.TRANSACTION_HISTORY, body.getMessage());
        assertEquals(1, body.getData().size());
        verify(transactionService).history(accountId);
    }

    @Test
    void statement_ShouldReturnStatement() {

        Long accountId = 1L;
        LocalDate fromDate = LocalDate.now().minusDays(10);
        LocalDate toDate = LocalDate.now();

        TransactionResponse transactionResponse = TransactionResponse.builder()
                        .transactionReferenceNumber("REF005")
                        .transactionType(Constant.DEPOSIT)
                        .transactionAmount(BigDecimal.valueOf(250))
                        .timestamp(LocalDateTime.now())
                        .build();

        List<TransactionResponse> serviceResponse = List.of(transactionResponse);
        when(transactionService.statement(accountId, fromDate, toDate)).thenReturn(serviceResponse);
        ResponseEntity<ApiResponse<List<TransactionResponse>>> response = transactionController.statement(accountId, fromDate, toDate);
        ApiResponse<List<TransactionResponse>> body = response.getBody();
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals(Constant.TRANSACTION_HISTORY, body.getMessage());
        assertEquals(1, body.getData().size());
        verify(transactionService).statement(accountId, fromDate, toDate);
    }
}
