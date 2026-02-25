package com.graduation.fer.domain.device;

import com.graduation.fer.domain.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"Devices\"")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"DeviceId\"", nullable = false)
    private Long deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"UserId\"", nullable = false)
    private User user;

    @Column(name = "\"DeviceType\"", length = 50, nullable = false)
    private String deviceType;

    @Column(name = "\"CreatedAt\"", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "\"UpdatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    public Long getDeviceId() { return deviceId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
