package com.app.eaglebank.dto.requests;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public class CreateTransactionRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "10000.00", message = "Amount cannot exceed 10000")
    private BigDecimal amount;

    @NotNull(message = "Currency is required")
    @Pattern(regexp = "GBP", message = "Only GBP is supported")
    private String currency;

    @NotNull(message = "Transaction type is required")
    @Pattern(regexp = "deposit|withdrawal", message = "Type must be 'deposit' or 'withdrawal'")
    private String type;

    private String reference;

    // Getters and setters
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
}
