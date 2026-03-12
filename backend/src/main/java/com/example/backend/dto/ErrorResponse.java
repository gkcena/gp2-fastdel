package com.example.backend.dto;

import java.time.Instant;

/**
 * Standard error response DTO matching FastDel API design rules.
 *
 * Example:
 * {
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Invalid credentials",
 *   "timestamp": "2025-01-01T12:00:00Z"
 * }
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp
) {
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, Instant.now());
    }
}
