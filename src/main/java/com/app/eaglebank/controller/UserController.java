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

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping
    public List<User> getAllUsers() {

        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable UUID userId, Authentication authentication) {
        User authUser = userService.getAuthenticatedUser(authentication);
        User targetUser = userService.getUserById(userId);

        userService.checkOwnership(authUser, targetUser);
        return ResponseEntity.ok(targetUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable UUID userId,
            @RequestBody User updatedUser,
            Authentication authentication
    ) {

        if (updatedUser.getName() == null && updatedUser.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field must be provided for update");
        }

        User authUser = userService.getAuthenticatedUser(authentication);
        User targetUser = userService.getUserById(userId);

        userService.checkOwnership(authUser, targetUser);
        User savedUser = userService.updateUser(targetUser, updatedUser);

        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        // 403 Authenticated user tries to delete someone else
        if (!authenticatedUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", 403, "error", "You are not authorized to delete this user"));
        }

        // 404 if user doesn't exist
        // 409 if user has bank accounts
        userService.deleteUserIfNoAccounts(userId);

        // 204 No Content — user deleted
        return ResponseEntity.noContent().build();
    }


}