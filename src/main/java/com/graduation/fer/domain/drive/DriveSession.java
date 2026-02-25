package com.graduation.fer.domain.drive;

import com.graduation.fer.domain.device.Device;
import com.graduation.fer.domain.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"DriveSessions\"")
public class DriveSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"DriveId\"", nullable = false)
    private Long driveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"UserId\"", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"DeviceId\"", nullable = false)
    private Device device;

    @Column(name = "\"StartedAt\"", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "\"EndedAt\"")
    private LocalDateTime endedAt;

    @Column(name = "\"CreatedAt\"", nullable = false)
    private LocalDateTime createdAt;

    public Long getDriveId() { return driveId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (startedAt == null) startedAt = LocalDateTime.now();
    }

}
