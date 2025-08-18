package com.app.eaglebank.security;

import com.app.eaglebank.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter for Eagle Bank API
 *
 * This filter intercepts incoming HTTP requests to validate JWT tokens and establish
 * the security context for authenticated users. It extends OncePerRequestFilter to
 * ensure it's executed only once per request, even in complex servlet environments.
 *
 * Key responsibilities:
 * - Extracts JWT tokens from the Authorization header
 * - Validates token authenticity and expiration
 * - Loads user details from the database
 * - Sets up Spring Security authentication context
 * - Bypasses authentication for public endpoints
 *
 * Security considerations:
 * - Only processes requests with valid Bearer tokens
 * - Validates tokens against user details to prevent token reuse after user changes
 * - Uses secure token extraction to prevent header injection attacks
 *
 */

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    // Constructor injection
    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();
        // Bypass filter for public endpoints
        if (path.startsWith("/v1/auth") || (path.equals("/v1/users"))) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the Authorization header from the request
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Validate Authorization header format - must be present and follow Bearer token pattern
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token by removing "Bearer " prefix (7 characters)
        final String jwt = authHeader.substring(7);

        // Extract user email from JWT token payload
        final String userEmail = jwtService.extractEmail(jwt);

        // Proceed with authentication only if:
        // 1. Email was successfully extracted from token
        // 2. No authentication is currently set in SecurityContext (avoid double processing)
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail); // Loads from DB

            // Validate the JWT token against the user details
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Create authentication token with user details and authorities
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                // Add request-specific details to the authentication token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in Spring Security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
