package com.graduation.fer.domain.music;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MusicRecommendationRepository extends JpaRepository<MusicRecommendation, Long> {

    Optional<MusicRecommendation> findTopByDriveSession_DriveIdOrderByIssuedAtDesc(Long driveId);
}
