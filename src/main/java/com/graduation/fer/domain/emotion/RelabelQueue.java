package com.graduation.fer.domain.emotion;

import com.graduation.fer.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"RelabelQueue\"")
public class RelabelQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"QueueId\"", nullable = false)
    private Long queueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"UserId\"", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"EmotionId\"", nullable = false)
    private Emotion emotion;

    @Column(name = "\"Reason\"", length = 50, nullable = false)
    private String reason; // LowConfidence / Conflict

    @Column(name = "\"Status\"", length = 20, nullable = false)
    private String status; // Open / Labeled / Discarded

    @Column(name = "\"CreatedAt\"", nullable = false)
    private LocalDateTime createdAt;

    public Long getQueueId() { return queueId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Emotion getEmotion() { return emotion; }
    public void setEmotion(Emotion emotion) { this.emotion = emotion; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
