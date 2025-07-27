package com.app.eaglebank.dto.requests;

import com.app.eaglebank.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateAccountRequest {

    @NotNull
    @NotBlank(message = "Account name is required")
    private String name;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}

