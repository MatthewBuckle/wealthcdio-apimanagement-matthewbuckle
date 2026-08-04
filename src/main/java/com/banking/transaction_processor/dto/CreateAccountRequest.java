package com.banking.transaction_processor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class CreateAccountRequest {

    @NotBlank(message = "Name cannot be empty")
    private String accountHolderName;

    @NotBlank(message = "Email cannot be empty")
    private String email;

}