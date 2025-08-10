package com.app.eaglebank.controller;

import com.app.eaglebank.model.User;
import com.app.eaglebank.repository.UserRepository;
import com.app.eaglebank.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for managing user operations in the Eagle Bank application.
 *
 * Provides endpoints for user registration, profile management, and account deletion
 * with proper authentication and authorization. Enforces ownership validation to ensure
 * users can only access and modify their own profile data, with special handling for
 * account-dependent user deletion to maintain data integrity.
 */

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        // Register new user with password hashing
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping
    public List<User> getAllUsers() {
        // Admin endpoint to retrieve all users
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        // Get authenticated user for ownership check
        User targetUser = userService.getUserById(userId);

        // Ensure user can only access their own profile
        userService.checkOwnership(authenticatedUser, targetUser);
        return ResponseEntity.ok(targetUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable UUID userId,
            @RequestBody User updatedUser,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        // Validate at least one field is provided for update
        if (updatedUser.getName() == null && updatedUser.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field must be provided for update");
        }

        // Get users and verify ownership
        User targetUser = userService.getUserById(userId);

        // Prevent users from updating other profiles
        userService.checkOwnership(authenticatedUser, targetUser);

        // Apply partial updates and save
        User savedUser = userService.updateUser(targetUser, updatedUser);

        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        // Prevent users from deleting other accounts
        if (!authenticatedUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", 403, "error", "You are not authorized to delete this user"));
        }

        // Delete user only if no bank accounts exist (maintains data integrity)
        userService.deleteUserIfNoAccounts(userId);

        // Return 204 No Content on successful deletion
        return ResponseEntity.noContent().build();
    }

}
