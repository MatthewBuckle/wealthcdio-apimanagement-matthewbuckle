package com.banking.transaction_processor.dto;

import lombok.Data;

@Data
public class CreateAccountRequest {

    private String accountHolderName;
    private String email;

}