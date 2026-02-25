package com.graduation.fer.domain.emotion;

import com.graduation.fer.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"CoachingEvents\"")
public class CoachingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"CoachingId\"", nullable = false)
    private Long coachingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"UserId\"", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"EmotionId\"")
    private Emotion emotion;

    @Column(name = "\"Type\"", length = 30, nullable = false)
    private String type; // Alert / Tip / Recommendation

    @Column(name = "\"Message\"", nullable = false, columnDefinition = "text")
    private String message;

    @Column(name = "\"Channel\"", length = 20, nullable = false)
    private String channel; // APP / Voice / Push

    @Column(name = "\"IssuedAt\"", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "\"CreatedAt\"", nullable = false)
    private LocalDateTime createdAt;

    public Long getCoachingId() { return coachingId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Emotion getEmotion() { return emotion; }
    public void setEmotion(Emotion emotion) { this.emotion = emotion; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
