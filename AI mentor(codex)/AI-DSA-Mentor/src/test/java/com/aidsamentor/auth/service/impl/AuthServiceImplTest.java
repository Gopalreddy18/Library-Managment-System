package com.aidsamentor.auth.service.impl;

import com.aidsamentor.auth.dto.AuthRequest;
import com.aidsamentor.auth.dto.AuthResponse;
import com.aidsamentor.auth.dto.RefreshTokenRequest;
import com.aidsamentor.auth.dto.RegisterRequest;
import com.aidsamentor.auth.entity.RefreshToken;
import com.aidsamentor.auth.repository.RefreshTokenRepository;
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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private RefreshTokenRepository refreshTokenRepository;
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        passwordEncoder = mock(PasswordEncoder.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        roleRepository = mock(RoleRepository.class);
        userRepository = mock(UserRepository.class);

        JwtProperties jwtProperties = new JwtProperties(
                "test-secret-key-with-at-least-thirty-two-characters",
                3_600_000,
                604_800_000
        );
        authService = new AuthServiceImpl(
                authenticationManager,
                jwtProperties,
                new JwtService(jwtProperties),
                passwordEncoder,
                refreshTokenRepository,
                roleRepository,
                userRepository
        );
    }

    @Test
    void registerCreatesUserWithEncodedPasswordAndIssuesTokens() {
        Role role = Role.builder().id(1L).name(RoleName.ROLE_USER).build();
        when(userRepository.existsByEmail("student@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.register(new RegisterRequest(
                "Student User",
                "student@example.com",
                "password123"
        ));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-password");
        assertThat(userCaptor.getValue().getRole().getName()).isEqualTo(RoleName.ROLE_USER);
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByEmail("student@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest(
                "Student User",
                "student@example.com",
                "password123"
        ))).isInstanceOf(BadRequestException.class)
                .hasMessage("Email is already registered");
    }

    @Test
    void loginAuthenticatesAndIssuesTokens() {
        Role role = Role.builder().name(RoleName.ROLE_USER).build();
        User user = User.builder()
                .email("student@example.com")
                .password("encoded-password")
                .fullName("Student User")
                .role(role)
                .enabled(true)
                .build();
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.login(new AuthRequest("student@example.com", "password123"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
    }

    @Test
    void refreshTokenRejectsExpiredToken() {
        RefreshToken expiredToken = RefreshToken.builder()
                .token("expired-token")
                .expiresAt(Instant.now().minusSeconds(1))
                .revoked(false)
                .user(User.builder()
                        .email("student@example.com")
                        .role(Role.builder().name(RoleName.ROLE_USER).build())
                        .enabled(true)
                        .build())
                .build();
        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> authService.refreshToken(new RefreshTokenRequest("expired-token")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Refresh token is expired or revoked");
        assertThat(expiredToken.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(expiredToken);
    }
}
