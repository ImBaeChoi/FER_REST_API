package com.graduation.fer.auth.dto;

import java.time.LocalDateTime;

public record UserMeResponse(
        String userId,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
