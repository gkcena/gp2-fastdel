package com.example.backend.dto;

/**
 * DTO for POST /api/v1/staff/change-password request body.
 */
public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
