package com.app.eaglebank.service;

import com.app.eaglebank.exception.ResourceNotFoundException;
import com.app.eaglebank.model.User;
import com.app.eaglebank.repository.AccountRepository;
import com.app.eaglebank.repository.UserRepository;
import com.app.eaglebank.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Service class responsible for managing user operations in the Eagle Bank application.
 *
 * Handles user registration, authentication, profile management, and account ownership validation.
 * Integrates with Spring Security for authentication and implements business logic for user
 * lifecycle management including secure password handling and account-dependent user deletion.
 */

@Service
public class UserService {

    // Repository dependencies for data access operations
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
    }

    public User registerUser(User user) {
        // Hash the password before saving for security
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserById(UUID userId) {
        // Fetch user by ID or return 404 if not found
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public void checkOwnership(User authUser, User targetUser) {
        // Prevent users from accessing/modifying other users' data
        if (!authUser.getId().equals(targetUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access or modify this user's details");
        }
    }

    public User updateUser(User existingUser, User updatedData) {
        // Update user fields if provided (partial update support)
        if (updatedData.getName() != null) {
            existingUser.setName(updatedData.getName());
        }
        if (updatedData.getPassword() != null) {
            // Re-hash password for security
            existingUser.setPassword(passwordEncoder.encode(updatedData.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    public List<User> getAllUsers() {
        // Admin function to retrieve all users
        return userRepository.findAll();
    }

    public void deleteUserIfNoAccounts(UUID userId) {
        // Find user or throw not found exception
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user has any existing bank accounts
        boolean hasAccounts = accountRepository.existsByUser(user);
        if (hasAccounts) {
            // Prevent deletion to maintain data integrity
            throw new BadRequestException("Cannot delete user with existing bank accounts");
        }

        // Safe to delete user with no accounts
        userRepository.delete(user);
    }
}
