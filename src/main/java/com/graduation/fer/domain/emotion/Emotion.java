package com.graduation.fer.domain.emotion;

import com.graduation.fer.domain.device.Device;
import com.graduation.fer.domain.drive.DriveSession;
import com.graduation.fer.domain.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"Emotions\"")
public class Emotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"EmotionId\"", nullable = false)
    private Long emotionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"UserId\"", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"DeviceId\"", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"DriveId\"")
    private DriveSession driveSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"EmotionTypeId\"", nullable = false)
    private EmotionType emotionType;

    @Column(name = "\"Confidence\"", nullable = false, precision = 4, scale = 3)
    private java.math.BigDecimal confidence;

    @Column(name = "\"Source\"", length = 20, nullable = false)
    private String source;

    @Column(name = "\"ModelVersion\"", length = 50, nullable = false)
    private String modelVersion;

    @Column(name = "\"OccurredAt\"", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "\"CreatedAt\"", nullable = false)
    private LocalDateTime createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"Payload\"", columnDefinition = "jsonb")
    private JsonNode payload;

    public Long getEmotionId() { return emotionId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    public DriveSession getDriveSession() { return driveSession; }
    public void setDriveSession(DriveSession driveSession) { this.driveSession = driveSession; }

    public EmotionType getEmotionType() { return emotionType; }
    public void setEmotionType(EmotionType emotionType) { this.emotionType = emotionType; }

    public java.math.BigDecimal getConfidence() { return confidence; }
    public void setConfidence(java.math.BigDecimal confidence) { this.confidence = confidence; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public JsonNode getPayload() { return payload; }
    public void setPayload(JsonNode payload) { this.payload = payload; }
}
