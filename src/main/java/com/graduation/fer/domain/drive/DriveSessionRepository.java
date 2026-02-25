package com.graduation.fer.domain.drive;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriveSessionRepository extends JpaRepository<DriveSession, Long> {

    Optional<DriveSession> findByDriveIdAndEndedAtIsNull(Long driveId);
    Optional<DriveSession> findByDriveIdAndUser_UserIdAndEndedAtIsNull(Long driveId, String userId);
    Optional<DriveSession> findByDriveId(Long driveId);
}
