package com.banking.transaction_processor.exception;

import com.banking.transaction_processor.constants.Constant;
import com.banking.transaction_processor.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> accountNotFound(AccountNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().message(ex.getMessage()).timestamp(LocalDateTime.now()).build());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> insufficientFunds(InsufficientFundsException ex) {

        return ResponseEntity.badRequest().body(ErrorResponse.builder().message(ex.getMessage()).timestamp(LocalDateTime.now()).build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgument(IllegalArgumentException ex) {

        return ResponseEntity.badRequest().body(ErrorResponse.builder().message(ex.getMessage()).timestamp(LocalDateTime.now()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(),e.getDefaultMessage()));

        return ResponseEntity.badRequest().body(ErrorResponse.builder().message(Constant.VALIDATION_FAILED).timestamp(LocalDateTime.now()).errors(errors).build());
    }
}
