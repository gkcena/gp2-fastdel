package com.example.backend.controller;

import com.example.backend.dto.ChangePasswordRequest;
import com.example.backend.dto.ErrorResponse;
import com.example.backend.dto.PhotoUploadResponse;
import com.example.backend.dto.StaffProfileResponse;
import com.example.backend.service.StaffProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Staff profile controller — /api/v1/staff
 * All endpoints require STAFF role (enforced by SecurityConfig).
 *
 * Rules enforced:
 *  • No business logic — delegates to StaffProfileService
 *  • DTO-only request/response
 */
@RestController
@RequestMapping("/api/v1/staff")
public class StaffProfileController {

    private final StaffProfileService staffProfileService;

    public StaffProfileController(StaffProfileService staffProfileService) {
        this.staffProfileService = staffProfileService;
    }

    /**
     * GET /api/v1/staff/profile — get current user's profile info.
     */
    @GetMapping("/profile")
    public ResponseEntity<StaffProfileResponse> getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(staffProfileService.getProfile(email));
    }

    /**
     * POST /api/v1/staff/change-password — change current user's password.
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            staffProfileService.changePassword(email, request.currentPassword(), request.newPassword());
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
        return ResponseEntity.ok(staffProfileService.uploadPhoto(email, photo));
    }
}
