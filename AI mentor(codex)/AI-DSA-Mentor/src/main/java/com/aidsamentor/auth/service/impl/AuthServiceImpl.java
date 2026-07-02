package com.aidsamentor.auth.service.impl;

import com.aidsamentor.auth.dto.AuthRequest;
import com.aidsamentor.auth.dto.AuthResponse;
import com.aidsamentor.auth.dto.LogoutRequest;
import com.aidsamentor.auth.dto.RefreshTokenRequest;
import com.aidsamentor.auth.dto.RegisterRequest;
import com.aidsamentor.auth.entity.RefreshToken;
import com.aidsamentor.auth.repository.RefreshTokenRepository;
import com.aidsamentor.auth.service.AuthService;
import com.aidsamentor.exception.BadRequestException;
import com.aidsamentor.exception.UnauthorizedException;
import com.aidsamentor.security.JwtProperties;
import com.aidsamentor.security.JwtService;
import com.aidsamentor.user.entity.Role;
import com.aidsamentor.user.entity.RoleName;
import com.aidsamentor.user.entity.User;
import com.aidsamentor.user.repository.RoleRepository;
import com.aidsamentor.user.repository.UserRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String BEARER = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email is already registered");
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build()));

        User user = User.builder()
                .email(request.email())
                .fullName(request.fullName())
                .password(passwordEncoder.encode(request.password()))
                .role(userRole)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        return issueTokens(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        return issueTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new UnauthorizedException("Refresh token is expired or revoked");
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        return issueTokens(refreshToken.getUser());
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenRepository.findByToken(request.refreshToken())
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshTokenRepository.save(refreshToken);
                });
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenRepository.save(RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plusMillis(jwtProperties.refreshExpirationMs()))
                .user(user)
                .build());

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                BEARER,
                jwtService.getAccessTokenExpirationMs()
        );
    }
}
