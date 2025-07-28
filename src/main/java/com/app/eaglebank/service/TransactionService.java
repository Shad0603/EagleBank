package com.app.eaglebank.service;

import com.app.eaglebank.dto.requests.CreateTransactionRequest;
import com.app.eaglebank.model.Account;
import com.app.eaglebank.model.Transaction;
import com.app.eaglebank.repository.AccountRepository;
import com.app.eaglebank.repository.TransactionRepository;
import com.app.eaglebank.exception.ResourceNotFoundException;
import com.app.eaglebank.model.User;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Service class responsible for handling transaction-related business logic in the Eagle Bank application.
 *
 * Includes creating deposit and withdrawal transactions for user accounts. This class ensures secure, atomic, and
 * validated transaction processing, adhering to banking domain rules and security constraints.
 */

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Transaction createTransaction(String accountNumber, CreateTransactionRequest request, User authenticatedUser) {
        // Retrieve account and validate its existence
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Check ownership
        if (!account.getUser().getId().equals(authenticatedUser.getId())) {
            throw new SecurityException("Access denied");
        }

        // Extract and validate transaction amount
        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than 0");
        }

        // Extract transaction type
        String type = request.getType();

        // Process deposit or withdrawal
        if (type.equalsIgnoreCase("deposit")) {
            account.setBalance(account.getBalance().add(amount));

        } else if (type.equalsIgnoreCase("withdrawal")) {
            // Check sufficient balance
            if (account.getBalance().compareTo(amount) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance for withdrawal");
            }
            account.setBalance(account.getBalance().subtract(amount));

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transaction type");
        }

        // Update account timestamp to reflect modification
        account.setUpdatedTimestamp(Instant.now());

        // Create and populate new transaction entity
        Transaction txn = new Transaction();
        txn.setAccount(account);
        txn.setAmount(amount);
        txn.setCurrency(request.getCurrency());
        txn.setType(type.toLowerCase());
        txn.setReference(request.getReference());
        txn.setTimestamp(Instant.now());
        txn.setId("tan-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));


        // Persist transaction and updated account in a single transaction
        txn = transactionRepository.save(txn);
        accountRepository.save(account);

        return txn;
    }
}
