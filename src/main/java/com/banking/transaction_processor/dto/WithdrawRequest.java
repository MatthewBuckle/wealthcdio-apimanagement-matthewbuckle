package com.banking.transaction_processor.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {

    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}
