package com.aidsamentor.auth.dto;

public record AuthResponse(
        String token,
        String tokenType
) {
}
