package com.banking.transaction_processor.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferRequest {

    @NotBlank
    private Long fromAccountId;

    @NotBlank
    private Long toAccountId;

    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}
