package com.graduation.fer.domain.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"RefreshTokens\"")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"Id\"")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"UserId\"", nullable = false)
    private User user;

    @Column(name = "\"Token\"", nullable = false, unique = true, length = 700)
    private String token;

    @Column(name = "\"ExpiresAt\"", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "\"Revoked\"", nullable = false)
    private boolean revoked = false;

    @Column(name = "\"CreatedAt\"", nullable = false)
    private LocalDateTime createdAt;

    // ===== Getter / Setter =====
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

}
