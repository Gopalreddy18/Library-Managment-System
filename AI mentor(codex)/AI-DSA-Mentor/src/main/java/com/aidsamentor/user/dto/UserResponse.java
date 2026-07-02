package com.aidsamentor.user.dto;

import com.aidsamentor.user.entity.RoleName;
import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        RoleName role,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {
}
