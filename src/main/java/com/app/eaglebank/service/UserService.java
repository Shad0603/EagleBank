package com.app.eaglebank.service;

import com.app.eaglebank.model.User;
import com.app.eaglebank.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Get authenticated user from Authentication
    public User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }

    // Fetch a user by ID or throw 404
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    // Enforce ownership
    public void checkOwnership(User authUser, User targetUser) {
        if (!authUser.getId().equals(targetUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access or modify this user's details");
        }
    }

    // Updating logic
    public User updateUser(User existingUser, User updatedData) {
        if (updatedData.getName() != null) {
            existingUser.setName(updatedData.getName());
        }
        if (updatedData.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(updatedData.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
