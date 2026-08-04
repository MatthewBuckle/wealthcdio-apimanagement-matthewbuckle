package com.banking.transaction_processor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {

    private String transactionReferenceNumber;
    private String transactionType;
    private BigDecimal transactionAmount;
    private LocalDateTime timestamp;
    private BigDecimal balance;
}