package com.app.eaglebank.controller;

import com.app.eaglebank.dto.requests.AuthRequest;
import com.app.eaglebank.dto.responses.AuthResponse;
import com.app.eaglebank.model.User;
import com.app.eaglebank.service.AuthService;
import com.app.eaglebank.service.JwtService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling authentication operations in the Eagle Bank application.
 *
 * Provides endpoints for user login and JWT token generation. Handles credential validation
 * and returns secure JWT tokens for authenticated sessions, enabling stateless authentication
 * for subsequent API requests.
 */

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        // Authenticate user with email and password
        User user = authService.authenticate(request.getEmail(), request.getPassword());

        // Generate JWT token for authenticated user
        String token = jwtService.generateToken(user);

        // Return token in response
        return new AuthResponse(token);
    }

}
