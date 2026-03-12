package com.example.backend.dto;

public record CreateStaffRequest(
        String name,
        String email,
        String password
) {
}
