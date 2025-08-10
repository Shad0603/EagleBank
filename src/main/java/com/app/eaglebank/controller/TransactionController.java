package com.app.eaglebank.controller;

import com.app.eaglebank.dto.requests.CreateTransactionRequest;
import com.app.eaglebank.dto.responses.TransactionResponse;
import com.app.eaglebank.exception.ResourceNotFoundException;
import com.app.eaglebank.model.Transaction;
import com.app.eaglebank.model.User;
import com.app.eaglebank.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing financial transactions in the Eagle Bank application.
 *
 * Handles transaction operations scoped to specific bank accounts with proper authentication
 * and authorization. Ensures users can only create transactions for accounts they own,
 * maintaining financial data security and account ownership validation.
 */

@RestController
@RequestMapping("/v1/accounts/{accountNumber}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    // Delegate transaction creation to the service layer
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @PathVariable String accountNumber,
            @Valid @RequestBody CreateTransactionRequest request,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        Transaction txn = transactionService.createTransaction(accountNumber, request, authenticatedUser);
        TransactionResponse response = new TransactionResponse(txn);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @PathVariable String accountNumber,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountNumber, authenticatedUser);
            // Convert entities to detailed responses
            List<TransactionResponse> response = transactions.stream()
                    .map(TransactionResponse::new)
                    .toList();

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

    }
}
