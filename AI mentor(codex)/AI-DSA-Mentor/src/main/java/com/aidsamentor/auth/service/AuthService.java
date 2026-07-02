package com.aidsamentor.auth.service;

import com.aidsamentor.auth.dto.AuthRequest;
import com.aidsamentor.auth.dto.AuthResponse;
import com.aidsamentor.auth.dto.LogoutRequest;
import com.aidsamentor.auth.dto.RefreshTokenRequest;
import com.aidsamentor.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(AuthRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(LogoutRequest request);
}
