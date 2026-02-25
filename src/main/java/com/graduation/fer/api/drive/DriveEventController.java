package com.graduation.fer.api.drive;

import com.graduation.fer.api.drive.dto.BatchUploadRequest;
import com.graduation.fer.api.drive.dto.BatchUploadResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drives")
public class DriveEventController {

    private final DriveEventIngestService ingestService;

    public DriveEventController(DriveEventIngestService ingestService) {
        this.ingestService = ingestService;
    }

    @PostMapping("/{driveId}/events:batch")
    public BatchUploadResponse uploadBatch(
            @PathVariable Long driveId,
            @Valid @RequestBody BatchUploadRequest request,
            Authentication authentication
    ) {
        String userId = authentication.getName(); // JWT에서 subject를 userId로 넣었다면 이게 userId
        return ingestService.ingest(driveId, userId, request);
    }
}
