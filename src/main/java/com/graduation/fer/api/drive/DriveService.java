package com.graduation.fer.api.drive;

import com.graduation.fer.api.drive.dto.DriveDetailResponse;
import com.graduation.fer.api.drive.dto.DriveEndResponse;
import com.graduation.fer.api.drive.dto.DriveStartRequest;
import com.graduation.fer.api.drive.dto.DriveStartResponse;
import com.graduation.fer.domain.device.Device;
import com.graduation.fer.domain.device.DeviceRepository;
import com.graduation.fer.domain.drive.DriveSession;
import com.graduation.fer.domain.drive.DriveSessionRepository;
import com.graduation.fer.domain.emotion.EmotionReport;
import com.graduation.fer.domain.emotion.EmotionReportRepository;
import com.graduation.fer.domain.emotion.EmotionRepository;
import com.graduation.fer.domain.user.User;
import com.graduation.fer.domain.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DriveService {

    private final DriveSessionRepository driveSessionRepository;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final EmotionRepository emotionRepository;
    private final EmotionReportRepository emotionReportRepository;

    public DriveService(
            DriveSessionRepository driveSessionRepository,
            UserRepository userRepository,
            DeviceRepository deviceRepository,
            EmotionRepository emotionRepository,
            EmotionReportRepository emotionReportRepository
    ) {
        this.driveSessionRepository = driveSessionRepository;
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.emotionRepository = emotionRepository;
        this.emotionReportRepository = emotionReportRepository;
    }

    /** 운전 시작 */
    public DriveStartResponse startDrive(String userId, DriveStartRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        LocalDateTime now = LocalDateTime.now();

        DriveSession session = new DriveSession();
        session.setUser(user);
        session.setDevice(device);
        session.setStartedAt(now);

        // ✅ DB에서 "CreatedAt NOT NULL" 이면 반드시 세팅해야 함
        session.setCreatedAt(now);

        DriveSession saved = driveSessionRepository.save(session);

        return new DriveStartResponse(
                saved.getDriveId(),
                saved.getStartedAt()
        );
    }

    /** 운전 종료 */
    public DriveEndResponse endDrive(String userId, Long driveId) {

        // findByDriveIdAndUser_UserIdAndEndedAtIsNull 으로 소유권 + 활성 상태 동시 검증
        DriveSession session = driveSessionRepository
                .findByDriveIdAndUser_UserIdAndEndedAtIsNull(driveId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Active drive not found"));

        LocalDateTime endTime = LocalDateTime.now();
        session.setEndedAt(endTime);

        long durationSeconds = Duration.between(session.getStartedAt(), endTime).getSeconds();

        return new DriveEndResponse(
                session.getDriveId(),
                session.getStartedAt(),
                endTime,
                durationSeconds
        );
    }

    @Transactional(readOnly = true)
    public DriveDetailResponse getDriveDetail(String userId, Long driveId) {

        DriveSession session = driveSessionRepository.findByDriveId(driveId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Drive not found"));

        // 본인 Drive인지 확인 (UserId가 varchar(100) = String 맞습니다)
        if (!session.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your drive session");
        }

        Long deviceId = session.getDevice().getDeviceId(); // Device PK getter 이름에 맞추세요
        var startedAt = session.getStartedAt();
        var endedAt = session.getEndedAt();

        Long durationSec = null;
        if (startedAt != null && endedAt != null) {
            durationSec = Duration.between(startedAt, endedAt).getSeconds();
        }

        long emotionCount = emotionRepository.countByDriveSession_DriveId(driveId);
        long reportCount = emotionReportRepository.countByDriveSession_DriveId(driveId);

        // 감정 분포
        List<Object[]> rows = emotionRepository.countByEmotionType(driveId);
        List<DriveDetailResponse.EmotionStat> dist = new ArrayList<>();
        for (Object[] r : rows) {
            Long emotionTypeId = ((Number) r[0]).longValue();
            String emotionName = (String) r[1];
            long cnt = ((Number) r[2]).longValue();

            double ratio = (emotionCount == 0) ? 0.0 : ((double) cnt / (double) emotionCount);
            dist.add(new DriveDetailResponse.EmotionStat(emotionTypeId, emotionName, cnt, ratio));
        }

        // 최신 리포트(있으면)
        DriveDetailResponse.LatestReport latest = null;
        var opt = emotionReportRepository.findTopByDriveSession_DriveIdOrderByGeneratedAtDesc(driveId);
        if (opt.isPresent()) {
            EmotionReport rep = opt.get();
            latest = new DriveDetailResponse.LatestReport(
                    rep.getReportId(),
                    rep.getWindowStart(),
                    rep.getWindowEnd(),
                    rep.getGeneratedAt(),
                    rep.getSummary()
            );
        }

        return new DriveDetailResponse(
                session.getDriveId(),
                deviceId,
                startedAt,
                endedAt,
                durationSec,
                emotionCount,
                reportCount,
                dist,
                latest
        );
    }
}
