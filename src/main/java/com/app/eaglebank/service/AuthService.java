package com.app.eaglebank.service;

import com.app.eaglebank.model.User;
import com.app.eaglebank.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    // Dependency to interact with the user database (e.g., fetching user by email)
    private final UserRepository userRepository;

    // Used to hash and verify passwords securely
    private final PasswordEncoder passwordEncoder;

    // Constructor injection of dependencies for better testability and immutability
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

        // Return the authenticated user object (can be used to generate a JWT)
        return user;
    }

    public User register(User user) {
        // encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

}
