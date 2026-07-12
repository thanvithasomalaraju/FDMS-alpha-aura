package com.madfood.backend.service;

import com.madfood.backend.dto.AuthRequest;
import com.madfood.backend.dto.AuthResponse;
import com.madfood.backend.dto.SignupRequest;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    AuthResponse register(SignupRequest request);
    void logout(String email);
    AuthResponse refreshToken(String token);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
}
