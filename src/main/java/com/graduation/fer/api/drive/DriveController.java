package com.graduation.fer.api.drive;

import com.graduation.fer.api.drive.dto.DriveDetailResponse;
import com.graduation.fer.api.drive.dto.DriveEndResponse;
import com.graduation.fer.api.drive.dto.DriveStartRequest;
import com.graduation.fer.api.drive.dto.DriveStartResponse;
import com.graduation.fer.api.drive.dto.MusicRecommendationResponse;
import com.graduation.fer.window.WindowControlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/drives")
public class DriveController {

    private final DriveService driveService;
    private final DriveMusicService driveMusicService;
    private final WindowControlService windowControlService;
    private final com.graduation.fer.domain.drive.DriveSessionRepository driveSessionRepository;

    public DriveController(
            DriveService driveService,
            DriveMusicService driveMusicService,
            WindowControlService windowControlService,
            com.graduation.fer.domain.drive.DriveSessionRepository driveSessionRepository
    ) {
        this.driveService = driveService;
        this.driveMusicService = driveMusicService;
        this.windowControlService = windowControlService;
        this.driveSessionRepository = driveSessionRepository;
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

    /** 현재 드라이브 세션의 최신 음악 추천 조회 */
    @GetMapping("/{driveId}/music")
    public MusicRecommendationResponse getLatestMusic(
            @AuthenticationPrincipal String userId,
            @PathVariable Long driveId
    ) {
        return driveMusicService.getLatestRecommendation(userId, driveId);
    }

    /** 창문 열기 수동 트리거 (테스트 및 외부 연동용) */
    @PostMapping("/{driveId}/window/open")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void openWindow(
            @AuthenticationPrincipal String userId,
            @PathVariable Long driveId
    ) {
        var drive = driveSessionRepository.findById(driveId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DriveSession not found"));
        if (!drive.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Drive does not belong to user");
        }
        windowControlService.openWindow();
    }
}
