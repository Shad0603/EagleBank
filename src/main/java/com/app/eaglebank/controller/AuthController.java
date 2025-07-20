package com.app.eaglebank.controller;

import com.app.eaglebank.dto.AuthRequest;
import com.app.eaglebank.dto.AuthResponse;
import com.app.eaglebank.model.User;
import com.app.eaglebank.service.AuthService;
import com.app.eaglebank.service.JwtService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        User user = authService.authenticate(request.getEmail(), request.getPassword());
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
