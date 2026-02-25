package com.graduation.fer.api.drive.dto;

import java.time.LocalDateTime;

public record DriveEndResponse(
        Long driveId,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        long durationSeconds
) {}
