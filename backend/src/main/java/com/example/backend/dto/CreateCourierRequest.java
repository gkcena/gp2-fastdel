package com.example.backend.dto;

public record CreateCourierRequest(
        String name,
        String email,
        String password
) {
}
