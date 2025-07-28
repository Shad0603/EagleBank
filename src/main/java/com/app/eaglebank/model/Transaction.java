package com.app.eaglebank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @NotNull
    @Pattern(regexp = "^tan-[A-Za-z0-9]+$")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String type; // "deposit" or "withdrawal"

    private String reference;

    @Column(nullable = false)
    private Instant timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    // Getters/setters
    public String getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getType() { return type; }
    public String getReference() { return reference; }
    public Instant getTimestamp() { return timestamp; }
    public Account getAccount() { return account; }

    public void setId(String id) { this.id = id; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setType(String type) { this.type = type; }
    public void setReference(String reference) { this.reference = reference; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public void setAccount(Account account) { this.account = account; }
}
