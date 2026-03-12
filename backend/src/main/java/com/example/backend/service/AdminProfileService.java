package com.example.backend.service;

import com.example.backend.dto.AdminProfileResponse;
import com.example.backend.dto.PhotoUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AdminProfileService {

    AdminProfileResponse getProfile(String email);

    void changePassword(String email, String currentPassword, String newPassword);

    PhotoUploadResponse uploadPhoto(String email, MultipartFile photo);
}
