package com.aidsamentor.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.security.jwt")
public record JwtProperties(
        String secretKey,
        long expirationMs,
        long refreshExpirationMs
) {
}
