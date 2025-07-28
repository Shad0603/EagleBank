package com.app.eaglebank.service;

import com.app.eaglebank.dto.requests.CreateAccountRequest;
import com.app.eaglebank.exception.ResourceNotFoundException;
import com.app.eaglebank.model.Account;
import com.app.eaglebank.model.User;
import com.app.eaglebank.repository.AccountRepository;
import com.app.eaglebank.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

/**
 * Service class responsible for managing bank account operations in the Eagle Bank application.
 *
 * This service handles account creation, retrieval, and validation operations while ensuring
 * proper user authorization and data integrity. It integrates with the Account and User
 * repositories to perform CRUD operations and implements business logic for account management.
 *
 * Key responsibilities:
 * - Creating new bank accounts with auto-generated account numbers
 * - Retrieving user-specific accounts with proper authorization checks
 * - Validating account ownership and access permissions
 * - Generating unique account numbers with standardized format
 */

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Account createAccount(CreateAccountRequest request, User user) {
        // Initialize new account entity
        Account account = new Account();
        account.setName(request.getName());
        account.setAccountType(request.getAccountType());
        account.setAccountNumber(generateAccountNumber());
        account.setSortCode("10-10-10");
        account.setCurrency("GBP");
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO); // Always 0 on creation

        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        // Generates a 6-digit random number and prefixes it with "01"
        String randomDigits = String.format("%06d", new Random().nextInt(1_000_000));
        return "01" + randomDigits;
    }

    public List<Account> getAccountsByUserEmail(String email) {
        // Find user by email or throw unauthorized exception
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"
                ));

        // Return all accounts for the authenticated user
        return accountRepository.findByUser(user);
    }

    public Account getUserAccountByAccountNumber(String accountNumber, User user) {
        // Find account by number or throw not found exception
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Verify account ownership to prevent unauthorized access
        if (!account.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to access this user's account details");
        }

        return account;
    }
}

