package com.graduation.fer.domain.biosignal;

import com.graduation.fer.domain.device.Device;
import com.graduation.fer.domain.drive.DriveSession;
import com.graduation.fer.domain.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"BioSignals\"")
public class BioSignal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"BioSignalId\"", nullable = false)
    private Long bioSignalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"UserId\"", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"DeviceId\"", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"DriveId\"")
    private DriveSession driveSession;

    @Column(name = "\"SignalType\"", length = 20, nullable = false)
    private String signalType; // HRV, GSR 등

    @Column(name = "\"Value\"", nullable = false)
    private Double value;

    @Column(name = "\"Unit\"", length = 20, nullable = false)
    private String unit;

    @Column(name = "\"MeasuredAt\"", nullable = false)
    private LocalDateTime measuredAt;

    @Column(name = "\"CreatedAt\"", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "\"QualityFlag\"", length = 30)
    private String qualityFlag;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"Attr\"", columnDefinition = "jsonb")
    private JsonNode attr;

    public Long getBioSignalId() { return bioSignalId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    public DriveSession getDriveSession() { return driveSession; }
    public void setDriveSession(DriveSession driveSession) { this.driveSession = driveSession; }

    public String getSignalType() { return signalType; }
    public void setSignalType(String signalType) { this.signalType = signalType; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDateTime getMeasuredAt() { return measuredAt; }
    public void setMeasuredAt(LocalDateTime measuredAt) { this.measuredAt = measuredAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getQualityFlag() { return qualityFlag; }
    public void setQualityFlag(String qualityFlag) { this.qualityFlag = qualityFlag; }

    public JsonNode getAttr() { return attr; }
    public void setAttr(JsonNode attr) { this.attr = attr; }
}
