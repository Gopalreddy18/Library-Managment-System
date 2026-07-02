package com.aidsamentor.security;

import com.aidsamentor.user.entity.Role;
import com.aidsamentor.user.entity.RoleName;
import com.aidsamentor.user.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(new JwtProperties(
            "test-secret-key-with-at-least-thirty-two-characters",
            3_600_000,
            604_800_000
    ));

    @Test
    void generateTokenCreatesValidAccessToken() {
        User user = User.builder()
                .email("student@example.com")
                .password("encoded-password")
                .fullName("Student User")
                .role(Role.builder().name(RoleName.ROLE_USER).build())
                .enabled(true)
                .build();

        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractUsername(token)).isEqualTo("student@example.com");
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
        assertThat(jwtService.getAccessTokenExpirationMs()).isEqualTo(3_600_000);
    }
}
