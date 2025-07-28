package com.app.eaglebank.dto.responses;

import com.app.eaglebank.model.Transaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionResponse {

    @NotNull
    @Pattern(regexp = "^tan-[A-Za-z0-9]+$")
    private String id;

    @NotNull
    private BigDecimal amount;

    private String currency;

    @NotNull
    private String type;

    private String reference;

    @NotNull
    @Pattern(regexp = "^usr-[A-Za-z0-9]+$")
    private String userId;

    @NotNull
    private Instant createdTimestamp;

    public TransactionResponse(Transaction txn) {
        this.id = txn.getId();
        this.amount = txn.getAmount();
        this.currency = txn.getCurrency();
        this.type = txn.getType();
        this.reference = txn.getReference();
        this.userId = txn.getAccount().getUser().getId().toString();
        this.createdTimestamp = txn.getTimestamp();
    }

    // Getters
    public String getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getType() { return type; }
    public String getReference() { return reference; }
    public String getUserId() { return userId; }
    public Instant getCreatedTimestamp() { return createdTimestamp; }

}

