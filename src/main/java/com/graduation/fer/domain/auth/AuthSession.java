package com.graduation.fer.domain.auth;

import com.graduation.fer.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"AuthSessions\"")
public class AuthSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"SessionId\"", nullable = false)
    private Long sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"UserId\"", nullable = false)
    private User user;

    @Column(name = "\"IssuedAt\"", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "\"ExpiresAt\"", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "\"IpAddress\"", length = 45)
    private String ipAddress;

    @Column(name = "\"UserAgent\"", columnDefinition = "text")
    private String userAgent;

    public Long getSessionId() { return sessionId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}
