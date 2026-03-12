package com.example.backend.service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
