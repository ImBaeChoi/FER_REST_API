package com.graduation.fer.api.drive;

import com.graduation.fer.api.drive.dto.DriveDetailResponse;
import com.graduation.fer.api.drive.dto.DriveEndResponse;
import com.graduation.fer.api.drive.dto.DriveStartRequest;
import com.graduation.fer.api.drive.dto.DriveStartResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drives")
public class DriveController {

    private final DriveService driveService;

    public DriveController(DriveService driveService) {
        this.driveService = driveService;
    }

    /** 운전 시작 */
    @PostMapping
    public DriveStartResponse startDrive(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody DriveStartRequest request
    ) {
        return driveService.startDrive(userId, request);
    }

    /** 운전 종료 */
    @PatchMapping("/{driveId}/end")
    public DriveEndResponse endDrive(
            @AuthenticationPrincipal String userId,
            @PathVariable Long driveId
    ) {
        return driveService.endDrive(userId, driveId);
    }

    @GetMapping("/{driveId}")
    public DriveDetailResponse getDriveDetail(
            @AuthenticationPrincipal String userId,
            @PathVariable Long driveId
    ) {
        return driveService.getDriveDetail(userId, driveId);
    }
}
