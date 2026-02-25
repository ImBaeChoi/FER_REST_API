package com.graduation.fer.api.drive.dto;

import jakarta.validation.constraints.NotNull;

public class DriveStartRequest {

    @NotNull
    private Long deviceId;

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
}
