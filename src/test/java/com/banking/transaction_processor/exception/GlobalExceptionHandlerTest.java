package com.banking.transaction_processor.exception;

import com.banking.transaction_processor.constants.Constant;
import com.banking.transaction_processor.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void accountNotFound_ShouldReturnNotFoundResponse() {

        AccountNotFoundException exception = new AccountNotFoundException(Constant.ACCOUNT_NOT_FOUND);
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.accountNotFound(exception);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(Constant.ACCOUNT_NOT_FOUND, body.getMessage());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void insufficientFunds_ShouldReturnBadRequestResponse() {

        InsufficientFundsException exception = new InsufficientFundsException(Constant.INSUFFICIENT_FUNDS);
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.insufficientFunds(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(Constant.INSUFFICIENT_FUNDS, body.getMessage());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void illegalArgument_ShouldReturnBadRequestResponse() {

        IllegalArgumentException exception = new IllegalArgumentException(Constant.INVALID_AMOUNT);
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.illegalArgument(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(Constant.INVALID_AMOUNT, body.getMessage());
        assertNotNull(body.getTimestamp());
    }
}
