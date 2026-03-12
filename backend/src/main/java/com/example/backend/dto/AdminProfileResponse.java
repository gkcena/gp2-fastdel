package com.example.backend.dto;

/**
 * DTO for GET /api/v1/admin/profile response body.
 */
public record AdminProfileResponse(
        String name,
        String email,
        String role,
        String profilePhotoUrl
) {
}
