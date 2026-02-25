package com.graduation.fer.domain.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"Users\"")
public class User {

    @Id
    @Column(name = "\"UserId\"", length = 100, nullable = false)
    private String userId;

    @Column(name = "\"Name\"", length = 100, nullable = false)
    private String name;

    @Column(name = "\"PasswordHash\"", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "\"CreatedAt\"", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "\"UpdatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    // ===== Getter / Setter =====
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
