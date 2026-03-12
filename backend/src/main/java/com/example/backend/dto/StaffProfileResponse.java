package com.example.backend.dto;

/**
 * DTO for GET /api/v1/staff/profile response body.
 */
public record StaffProfileResponse(
        String name,
        String email,
        String role,
        String profilePhotoUrl
) {
}
