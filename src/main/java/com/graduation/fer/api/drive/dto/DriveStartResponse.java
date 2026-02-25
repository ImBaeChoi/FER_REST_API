package com.graduation.fer.api.drive.dto;

import java.time.LocalDateTime;

public class DriveStartResponse {

    private Long driveId;
    private LocalDateTime startedAt;

    public DriveStartResponse(Long driveId, LocalDateTime startedAt) {
        this.driveId = driveId;
        this.startedAt = startedAt;
    }

    public Long getDriveId() { return driveId; }
    public LocalDateTime getStartedAt() { return startedAt; }
}
