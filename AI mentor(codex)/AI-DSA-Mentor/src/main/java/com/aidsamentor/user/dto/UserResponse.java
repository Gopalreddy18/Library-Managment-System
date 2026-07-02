package com.aidsamentor.user.dto;

import com.aidsamentor.user.entity.Role;
import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        Role role,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {
}
