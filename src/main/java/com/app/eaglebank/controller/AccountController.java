package com.app.eaglebank.controller;

import com.app.eaglebank.dto.requests.CreateAccountRequest;
import com.app.eaglebank.dto.responses.AccountResponse;
import com.app.eaglebank.mapper.AccountMapper;
import com.app.eaglebank.model.Account;
import com.app.eaglebank.model.User;
import com.app.eaglebank.repository.UserRepository;
import com.app.eaglebank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing bank account operations in the Eagle Bank application.
 *
 * Provides endpoints for account creation, retrieval, and management with proper authentication
 * and authorization. All endpoints require user authentication and enforce account ownership
 * validation to ensure users can only access their own account data.
 */

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public AccountController(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @AuthenticationPrincipal User user
    ) {
        // Create new account for authenticated user
        Account account = accountService.createAccount(request, user);

        // Return created account with 201 status
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AccountResponse(account));
    }

    // ADMIN ENDPOINT ONLY FOR TEST PURPOSES
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts(
            @AuthenticationPrincipal UserDetails userDetails) {

        // Extract email from authenticated user
        String email = userDetails.getUsername();

        // Get all accounts belonging to the user
        List<Account> accounts = accountService.getAccountsByUserEmail(email);

        // Convert to response DTOs
        List<AccountResponse> response = accounts.stream()
                .map(AccountResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByAccountNumber(
            @PathVariable String accountNumber,
            @AuthenticationPrincipal User authenticatedUser) {

        // Fetch account with ownership validation
        Account account = accountService.getUserAccountByAccountNumber(accountNumber, authenticatedUser);

        // Map to response DTO and return
        AccountResponse response = accountMapper.toResponse(account);
        return ResponseEntity.ok(response);
    }

}

