package com.app.eaglebank.config;

import com.app.eaglebank.repository.UserRepository;
import com.app.eaglebank.security.CustomUserDetailsService;
import com.app.eaglebank.security.JwtAuthenticationFilter;
import com.app.eaglebank.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application configuration class for Eagle Bank security and authentication components.
 *
 * Defines and configures essential beans for JWT-based authentication, password encoding,
 * and user details management. Centralizes the configuration of security-related services
 * to ensure proper dependency injection and consistent security setup across the application.
 */

@Configuration
public class AppConfig {

    private final UserRepository userRepository;

    public AppConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }

    @Bean
    public CustomUserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        return new JwtAuthenticationFilter(jwtService, customUserDetailsService);
    }
}
