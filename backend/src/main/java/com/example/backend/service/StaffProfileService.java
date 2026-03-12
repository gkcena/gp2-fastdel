package com.example.backend.service;

import com.example.backend.dto.PhotoUploadResponse;
import com.example.backend.dto.StaffProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface StaffProfileService {

    StaffProfileResponse getProfile(String email);

    void changePassword(String email, String currentPassword, String newPassword);

    PhotoUploadResponse uploadPhoto(String email, MultipartFile photo);
}
