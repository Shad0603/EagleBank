package com.app.eaglebank.service;

import com.app.eaglebank.dto.requests.CreateAccountRequest;
import com.app.eaglebank.exception.ResourceNotFoundException;
import com.app.eaglebank.model.Account;
import com.app.eaglebank.model.AccountType;
import com.app.eaglebank.model.User;
import com.app.eaglebank.repository.AccountRepository;
import com.app.eaglebank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------- Test: createAccount --------
    @Test
    void createAccount_shouldCreateAccountWithDefaults() {
        // Arrange
        User user = new User();
        CreateAccountRequest request = new CreateAccountRequest();
        request.setName("My Savings");
        request.setAccountType(AccountType.PERSONAL);

        // Capture the account saved
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Account created = accountService.createAccount(request, user);

        // Assert
        verify(accountRepository).save(accountCaptor.capture());
        Account saved = accountCaptor.getValue();

        assertThat(saved.getName()).isEqualTo("My Savings");
        assertThat(saved.getAccountType()).isEqualTo(AccountType.PERSONAL);
        assertThat(saved.getSortCode()).isEqualTo("10-10-10");
        assertThat(saved.getCurrency()).isEqualTo("GBP");
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(saved.getAccountNumber()).startsWith("01");
    }

    // -------- Test: getAccountsByUserEmail --------
    @Test
    void getAccountsByUserEmail_shouldReturnAccounts_whenUserExists() {
        // Arrange
        String email = "user@example.com";
        User user = new User();
        List<Account> accounts = List.of(new Account(), new Account());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(accountRepository.findByUser(user)).thenReturn(accounts);

        // Act
        List<Account> result = accountService.getAccountsByUserEmail(email);

        // Assert
        assertThat(result).hasSize(2);
        verify(accountRepository).findByUser(user);
    }

    @Test
    void getAccountsByUserEmail_shouldThrow_whenUserNotFound() {
        // Arrange
        String email = "unknown@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> accountService.getAccountsByUserEmail(email))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Authenticated user not found");
    }

    // -------- Test: getUserAccountByAccountNumber --------
    @Test
    void getUserAccountByAccountNumber_shouldReturnAccount_whenOwnerMatches() {
        // Arrange
        String accountNumber = "01123456";
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Account account = new Account();
        account.setUser(user);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        // Act
        Account result = accountService.getUserAccountByAccountNumber(accountNumber, user);

        // Assert
        assertThat(result).isEqualTo(account);
    }

    @Test
    void getUserAccountByAccountNumber_shouldThrow_whenAccountNotFound() {
        // Arrange
        String accountNumber = "01999999";
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> accountService.getUserAccountByAccountNumber(accountNumber, new User()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Account not found");
    }

    @Test
    void getUserAccountByAccountNumber_shouldThrow_whenUserNotOwner() {
        // Arrange
        UUID ownerId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        User owner = new User(); owner.setId(ownerId);
        User requester = new User(); requester.setId(otherUserId);

        Account account = new Account(); account.setUser(owner);
        when(accountRepository.findByAccountNumber("01111111")).thenReturn(Optional.of(account));

        // Act + Assert
        assertThatThrownBy(() -> accountService.getUserAccountByAccountNumber("01111111", requester))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("You are not authorized");
    }
}

