package com.example.backend.controller;

import com.example.backend.dto.AdminProfileResponse;
import com.example.backend.dto.ChangePasswordRequest;
import com.example.backend.dto.ErrorResponse;
import com.example.backend.dto.PhotoUploadResponse;
import com.example.backend.service.AdminProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin profile controller — /api/v1/admin
 * All endpoints require ADMIN role (enforced by SecurityConfig).
 *
 * Rules enforced:
 *  • No business logic — delegates to AdminProfileService
 *  • DTO-only request/response
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminProfileController {

    private final AdminProfileService adminProfileService;

    public AdminProfileController(AdminProfileService adminProfileService) {
        this.adminProfileService = adminProfileService;
    }

    @GetMapping("/profile")
    public ResponseEntity<AdminProfileResponse> getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(adminProfileService.getProfile(email));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            adminProfileService.changePassword(email, request.currentPassword(), request.newPassword());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping(value = "/profile/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoUploadResponse> uploadPhoto(@RequestParam("photo") MultipartFile photo) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(adminProfileService.uploadPhoto(email, photo));
    }
}
