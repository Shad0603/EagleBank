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
import java.util.UUID;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    public AccountController(AccountService accountService, UserRepository userRepository, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.userRepository = userRepository;
        this.accountMapper = accountMapper;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @AuthenticationPrincipal User user
    ) {
        // Save the new account
        Account account = accountService.createAccount(request, user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AccountResponse(account));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        List<Account> accounts = accountService.getAccountsByUserEmail(email);
        List<AccountResponse> response = accounts.stream()
                .map(AccountResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByAccountNumber(
            @PathVariable String accountNumber,
            @AuthenticationPrincipal User authenticatedUser) {

        Account account = accountService.getUserAccountByAccountNumber(accountNumber, authenticatedUser);

        AccountResponse response = accountMapper.toResponse(account);
        return ResponseEntity.ok(response);
    }



}
