package com.app.eaglebank.service;

import com.app.eaglebank.dto.requests.CreateTransactionRequest;
import com.app.eaglebank.exception.ResourceNotFoundException;
import com.app.eaglebank.model.Account;
import com.app.eaglebank.model.Transaction;
import com.app.eaglebank.model.User;
import com.app.eaglebank.repository.AccountRepository;
import com.app.eaglebank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private TransactionService transactionService;

    private User user;
    private User otherUser;
    private Account account;


    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        accountRepository = mock(AccountRepository.class);
        transactionService = new TransactionService(transactionRepository, accountRepository);

        user = new User();
        user.setId(UUID.randomUUID());

        account = new Account();
        account.setUser(user);
        account.setAccountNumber("01000001");
        account.setBalance(BigDecimal.valueOf(500)); // Set a default initial value of £500
    }

    @Test
    void testSuccessfulDeposit() {
        // Arrange
        CreateTransactionRequest req = new CreateTransactionRequest();
        req.setAmount(BigDecimal.valueOf(100));
        req.setCurrency("GBP");
        req.setType("deposit");
        req.setReference("Top-up");

        when(accountRepository.findByAccountNumber("01000001")).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Transaction txn = transactionService.createTransaction("01000001", req, user);

        // Assert
        assertEquals("deposit", txn.getType());
        assertEquals(BigDecimal.valueOf(600), account.getBalance());
        assertNotNull(txn.getId());
    }

    @Test
    void testSuccessfulWithdrawal() {
        CreateTransactionRequest req = new CreateTransactionRequest();
        req.setAmount(BigDecimal.valueOf(200));
        req.setCurrency("GBP");
        req.setType("withdrawal");
        req.setReference("Shopping");

        when(accountRepository.findByAccountNumber("01000001")).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction txn = transactionService.createTransaction("01000001", req, user);

        assertEquals("withdrawal", txn.getType());
        assertEquals(BigDecimal.valueOf(300), account.getBalance());
    }

    @Test
    void testWithdrawalInsufficientBalanceThrowsException() {
        CreateTransactionRequest req = new CreateTransactionRequest();
        req.setAmount(BigDecimal.valueOf(800));
        req.setCurrency("GBP");
        req.setType("withdrawal");
        req.setReference("Big Purchase");

        when(accountRepository.findByAccountNumber("01000001")).thenReturn(Optional.of(account));

        var ex = assertThrows(Exception.class,
                () -> transactionService.createTransaction("01000001", req, user)
        );
        assertTrue(ex.getMessage().contains("Insufficient balance"));
    }

    @Test
    void testInvalidTransactionTypeThrowsException() {
        CreateTransactionRequest req = new CreateTransactionRequest();
        req.setAmount(BigDecimal.valueOf(100));
        req.setCurrency("GBP");
        req.setType("transfer"); // invalid
        req.setReference("Unknown");

        when(accountRepository.findByAccountNumber("01000001")).thenReturn(Optional.of(account));

        var ex = assertThrows(Exception.class,
                () -> transactionService.createTransaction("01000001", req, user)
        );
        assertTrue(ex.getMessage().contains("Invalid transaction type"));
    }

    @Test
    void testNonexistentAccountThrowsException() {
        CreateTransactionRequest req = new CreateTransactionRequest();
        req.setAmount(BigDecimal.valueOf(100));
        req.setCurrency("GBP");
        req.setType("deposit");
        req.setReference("Missing account");

        when(accountRepository.findByAccountNumber("00000000")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.createTransaction("00000000", req, user)
        );
    }

    @Test
    void testUnauthorizedUserThrowsException() {
        CreateTransactionRequest req = new CreateTransactionRequest();
        req.setAmount(BigDecimal.valueOf(100));
        req.setCurrency("GBP");
        req.setType("deposit");
        req.setReference("Fraud?");

        // Simulate different user
        User intruder = new User();
        intruder.setId(UUID.randomUUID());

        when(accountRepository.findByAccountNumber("01000001")).thenReturn(Optional.of(account));

        assertThrows(SecurityException.class,
                () -> transactionService.createTransaction("01000001", req, intruder)
        );
    }

    @Test
    void testZeroAmountThrowsException() {
        CreateTransactionRequest req = new CreateTransactionRequest();
        req.setAmount(BigDecimal.ZERO);
        req.setCurrency("GBP");
        req.setType("deposit");
        req.setReference("No funds");

        when(accountRepository.findByAccountNumber("01000001")).thenReturn(Optional.of(account));

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.createTransaction("01000001", req, user)
        );
    }

    @Test
    void testGetTransactionsByAccountId_OwnAccount_Success() {
        // Arrange
        Transaction tx = new Transaction();
        tx.setId("tan-abc123");

        List<Transaction> transactions = List.of(tx);

        when(accountRepository.findByAccountNumber("123456789"))
                .thenReturn(Optional.of(account));
        when(transactionRepository.findByAccount_AccountNumber("123456789"))
                .thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByAccountId("123456789", user);

        // Assert
        assertEquals(1, result.size());
        assertEquals("tan-abc123", result.get(0).getId());
    }

    @Test
    void testGetTransactionsByAccountId_AccountNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(accountRepository.findByAccountNumber("123456789"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                transactionService.getTransactionsByAccountId("123456789", user)
        );
    }
}
