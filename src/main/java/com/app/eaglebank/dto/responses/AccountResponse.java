package com.app.eaglebank.dto.responses;

import com.app.eaglebank.model.Account;

import java.math.BigDecimal;
import java.time.Instant;

public class AccountResponse {
    private String accountNumber;
    private String sortCode = "10-10-10";
    private String name;
    private String accountType;
    private BigDecimal balance;
    private String currency = "GBP";
    private Instant createdTimestamp;
    private Instant updatedTimestamp;

    // Constructor
    public AccountResponse(Account account) {
        this.accountNumber = account.getAccountNumber();
        this.sortCode = account.getSortCode();
        this.name = account.getName();
        this.accountType = account.getAccountType().name().toLowerCase();
        this.balance = account.getBalance();
        this.currency = account.getCurrency();
        this.createdTimestamp = account.getCreatedTimestamp();
        this.updatedTimestamp = account.getUpdatedTimestamp();
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getSortCode() { return sortCode; }
    public String getName() { return name; }
    public String getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
    public String getCurrency() { return currency; }
    public Instant getCreatedTimestamp() { return createdTimestamp; }
    public Instant getUpdatedTimestamp() { return updatedTimestamp; }
}

