package com.app.eaglebank.service;

import com.app.eaglebank.model.User;
import com.app.eaglebank.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for user authentication operations in the Eagle Bank application.
 *
 * Handles secure user authentication using email and password credentials with proper
 * password hashing verification. Implements security best practices by providing generic
 * error messages to prevent user enumeration attacks.
 */

@Service
public class AuthService {

    // Dependency to interact with the user database (e.g., fetching user by email)
    private final UserRepository userRepository;

    // Used to hash and verify passwords securely
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Authenticates a user by email and password
    public User authenticate(String email, String rawPassword) {
        // Look up the user by email; if not found, throw a generic "invalid credentials" exception
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Check if the provided password matches the stored (encoded) password
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            // If not, throw the same generic error (avoids revealing whether email or password was wrong)
            throw new RuntimeException("Invalid credentials");
        }

        // Return the authenticated user object
        return user;
    }

}

