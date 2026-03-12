package com.example.backend.dto;

/**
 * DTO for POST /api/v1/auth/login request body.
 */
public record LoginRequest(
        String email,
        String password
) {
}
