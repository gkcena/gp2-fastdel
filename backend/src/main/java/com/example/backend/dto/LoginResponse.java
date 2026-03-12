package com.example.backend.dto;

/**
 * DTO for POST /api/v1/auth/login response body.
 */
public record LoginResponse(
        String token,
        long expiresIn,
        String role,
        String name
) {
}
