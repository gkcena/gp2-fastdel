package com.example.backend.service.impl;

import com.example.backend.dto.AdminProfileResponse;
import com.example.backend.dto.PhotoUploadResponse;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.AdminProfileService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class AdminProfileServiceImpl implements AdminProfileService {

    private static final String UPLOADS_DIR = "uploads";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminProfileServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AdminProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new AdminProfileResponse(
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getProfilePhotoUrl()
        );
    }

    @Override
    public void changePassword(String email, String currentPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public PhotoUploadResponse uploadPhoto(String email, MultipartFile photo) {
        if (photo.isEmpty()) {
            throw new IllegalArgumentException("Photo file is required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        try {
            Path uploadPath = Paths.get(UPLOADS_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = photo.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID() + extension;

            Path filePath = uploadPath.resolve(filename);
            Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String photoUrl = "/uploads/" + filename;
            user.setProfilePhotoUrl(photoUrl);
            userRepository.save(user);

            return new PhotoUploadResponse(photoUrl);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save photo", e);
        }
    }
}
