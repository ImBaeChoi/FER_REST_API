package com.graduation.fer.api.drive;

import com.graduation.fer.api.drive.dto.BatchUploadRequest;
import com.graduation.fer.api.drive.dto.BatchUploadResponse;
import com.graduation.fer.domain.biosignal.BioSignal;
import com.graduation.fer.domain.biosignal.BioSignalRepository;
import com.graduation.fer.domain.device.Device;
import com.graduation.fer.domain.device.DeviceRepository;
import com.graduation.fer.domain.drive.DriveSession;
import com.graduation.fer.domain.drive.DriveSessionRepository;
import com.graduation.fer.domain.emotion.*;
import com.graduation.fer.domain.user.User;
import com.graduation.fer.domain.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class DriveEventIngestService {

    private final UserRepository userRepository;
    private final DriveSessionRepository driveSessionRepository;
    private final DeviceRepository deviceRepository;
    private final EmotionTypeRepository emotionTypeRepository;
    private final EmotionRepository emotionRepository;
    private final BioSignalRepository bioSignalRepository;

    public DriveEventIngestService(
            UserRepository userRepository,
            DriveSessionRepository driveSessionRepository,
            DeviceRepository deviceRepository,
            EmotionTypeRepository emotionTypeRepository,
            EmotionRepository emotionRepository,
            BioSignalRepository bioSignalRepository
    ) {
        this.userRepository = userRepository;
        this.driveSessionRepository = driveSessionRepository;
        this.deviceRepository = deviceRepository;
        this.emotionTypeRepository = emotionTypeRepository;
        this.emotionRepository = emotionRepository;
        this.bioSignalRepository = bioSignalRepository;
    }

    @Transactional
    public BatchUploadResponse ingest(Long driveId, String userId, BatchUploadRequest req) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        DriveSession drive = driveSessionRepository.findById(driveId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DriveSession not found"));

        // drive가 해당 user 소유인지 체크 (중요)
        if (!drive.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Drive does not belong to user");
        }

        // ===== deviceId, emotionTypeId를 배치로 미리 조회해서 DB 왕복 최소화 =====
        Set<Long> deviceIds = new HashSet<>();
        Set<Short> emotionTypeIds = new HashSet<>();

        if (req.getEmotions() != null) {
            for (var e : req.getEmotions()) {
                deviceIds.add(e.getDeviceId());
                emotionTypeIds.add(e.getEmotionTypeId());
            }
        }
        if (req.getBiosignals() != null) {
            for (var b : req.getBiosignals()) {
                deviceIds.add(b.getDeviceId());
            }
        }

        Map<Long, Device> deviceMap = deviceRepository.findAllById(deviceIds).stream()
                .collect(java.util.stream.Collectors.toMap(Device::getDeviceId, d -> d));

        Map<Short, EmotionType> emotionTypeMap = emotionTypeRepository.findAllById(emotionTypeIds).stream()
                .collect(java.util.stream.Collectors.toMap(EmotionType::getEmotionTypeId, t -> t));

        // 소유권 체크: device도 user 소유인지 체크
        for (Device d : deviceMap.values()) {
            if (!d.getUser().getUserId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Device does not belong to user");
            }
        }

        List<Emotion> emotionsToSave = new ArrayList<>();
        List<BioSignal> biosToSave = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        // ===== Emotions =====
        if (req.getEmotions() != null && !req.getEmotions().isEmpty()) {
            for (var dto : req.getEmotions()) {

                Device device = deviceMap.get(dto.getDeviceId());
                if (device == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Device not found: " + dto.getDeviceId());

                EmotionType type = emotionTypeMap.get(dto.getEmotionTypeId());
                if (type == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EmotionType not found: " + dto.getEmotionTypeId());

                Emotion e = new Emotion();
                e.setUser(user);
                e.setDevice(device);
                e.setDriveSession(drive);
                e.setEmotionType(type);

                e.setSource(dto.getSource());
                e.setModelVersion(dto.getModelVersion());
                e.setOccurredAt(parseIso(dto.getOccurredAt()));
                e.setConfidence(new BigDecimal(dto.getConfidence()));
                e.setCreatedAt(now);
                e.setPayload(dto.getPayload());

                emotionsToSave.add(e);
            }
            emotionRepository.saveAll(emotionsToSave);
        }

        // ===== BioSignals =====
        if (req.getBiosignals() != null && !req.getBiosignals().isEmpty()) {
            for (var dto : req.getBiosignals()) {
                Device device = deviceMap.get(dto.getDeviceId());
                if (device == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Device not found: " + dto.getDeviceId());

                BioSignal b = new BioSignal();
                b.setUser(user);
                b.setDevice(device);
                b.setDriveSession(drive);

                b.setSignalType(dto.getSignalType());
                b.setValue(dto.getValue());
                b.setUnit(dto.getUnit());
                b.setMeasuredAt(parseIso(dto.getMeasuredAt()));
                b.setCreatedAt(now);
                b.setQualityFlag(dto.getQualityFlag());
                b.setAttr(dto.getAttr());

                biosToSave.add(b);
            }
            bioSignalRepository.saveAll(biosToSave);
        }

        return new BatchUploadResponse(emotionsToSave.size(), biosToSave.size());
    }

    private LocalDateTime parseIso(String iso) {
        try {
            return LocalDateTime.parse(iso);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid datetime format: " + iso);
        }
    }
}
