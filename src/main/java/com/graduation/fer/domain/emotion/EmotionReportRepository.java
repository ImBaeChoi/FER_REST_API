package com.graduation.fer.domain.emotion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmotionReportRepository extends JpaRepository<EmotionReport, Long> {

    long countByDriveSession_DriveId(Long driveId);

    Optional<EmotionReport> findTopByDriveSession_DriveIdOrderByGeneratedAtDesc(Long driveId);
}
