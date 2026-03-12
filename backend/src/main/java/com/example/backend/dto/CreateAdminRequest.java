package com.example.backend.dto;

public record CreateAdminRequest(
        String name,
        String email,
        String password
) {
}
