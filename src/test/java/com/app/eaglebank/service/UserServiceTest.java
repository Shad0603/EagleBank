package com.app.eaglebank.service;

import com.app.eaglebank.exception.BadRequestException;
import com.app.eaglebank.exception.ResourceNotFoundException;
import com.app.eaglebank.model.User;
import com.app.eaglebank.repository.AccountRepository;
import com.app.eaglebank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setPassword("plainPassword");
    }

    @Test
    void registerUser_shouldEncodePasswordAndSave() {
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User saved = userService.registerUser(user);

        assertEquals("hashed", saved.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User found = userService.getUserById(userId);

        assertEquals(userId, found.getId());
    }

    @Test
    void getUserById_shouldThrow_whenNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void checkOwnership_shouldPass_whenSameUser() {
        assertDoesNotThrow(() -> userService.checkOwnership(user, user));
    }

    @Test
    void checkOwnership_shouldThrow_whenDifferentUser() {
        User other = new User();
        other.setId(UUID.randomUUID());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            userService.checkOwnership(user, other);
        });

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void updateUser_shouldUpdateFieldsAndEncodePassword() {
        User updated = new User();
        updated.setName("Updated Name");
        updated.setPassword("newPassword");

        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(user, updated);

        assertEquals("Updated Name", result.getName());
        assertEquals("encodedPass", result.getPassword());
    }

    @Test
    void getAllUsers_shouldReturnList() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getId());
    }

    @Test
    void deleteUserIfNoAccounts_shouldDelete_whenNoAccounts() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.existsByUser(user)).thenReturn(false);

        userService.deleteUserIfNoAccounts(userId);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserIfNoAccounts_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUserIfNoAccounts(userId);
        });
    }

    @Test
    void deleteUserIfNoAccounts_shouldThrow_whenUserHasAccounts() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.existsByUser(user)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            userService.deleteUserIfNoAccounts(userId);
        });
    }
}

