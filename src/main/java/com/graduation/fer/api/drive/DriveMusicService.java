package com.graduation.fer.api.drive;

import com.graduation.fer.api.drive.dto.MusicRecommendationResponse;
import com.graduation.fer.domain.drive.DriveSessionRepository;
import com.graduation.fer.domain.music.MusicRecommendationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DriveMusicService {

    private final DriveSessionRepository driveSessionRepository;
    private final MusicRecommendationRepository musicRecommendationRepository;

    public DriveMusicService(
            DriveSessionRepository driveSessionRepository,
            MusicRecommendationRepository musicRecommendationRepository
    ) {
        this.driveSessionRepository = driveSessionRepository;
        this.musicRecommendationRepository = musicRecommendationRepository;
    }

    @Transactional(readOnly = true)
    public MusicRecommendationResponse getLatestRecommendation(String userId, Long driveId) {
        var drive = driveSessionRepository.findById(driveId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DriveSession not found"));

        if (!drive.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Drive does not belong to user");
        }

        return musicRecommendationRepository
                .findTopByDriveSession_DriveIdOrderByIssuedAtDesc(driveId)
                .map(MusicRecommendationResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No music recommendation yet"));
    }
}
