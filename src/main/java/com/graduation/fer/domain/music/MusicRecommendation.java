package com.graduation.fer.domain.music;

import com.graduation.fer.domain.drive.DriveSession;
import com.graduation.fer.domain.emotion.Emotion;
import com.graduation.fer.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"MusicRecommendations\"")
public class MusicRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"RecommendationId\"", nullable = false)
    private Long recommendationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"UserId\"", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"EmotionId\"")
    private Emotion emotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"DriveId\"")
    private DriveSession driveSession;

    @Column(name = "\"EmotionName\"", length = 30, nullable = false)
    private String emotionName;

    @Column(name = "\"Genre\"", length = 50, nullable = false)
    private String genre;

    @Column(name = "\"PlaylistId\"", length = 100)
    private String playlistId;

    @Column(name = "\"PlaylistName\"", length = 255)
    private String playlistName;

    @Column(name = "\"PlaylistUrl\"", length = 500)
    private String playlistUrl;

    @Column(name = "\"IssuedAt\"", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "\"CreatedAt\"", nullable = false)
    private LocalDateTime createdAt;

    public Long getRecommendationId() { return recommendationId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Emotion getEmotion() { return emotion; }
    public void setEmotion(Emotion emotion) { this.emotion = emotion; }

    public DriveSession getDriveSession() { return driveSession; }
    public void setDriveSession(DriveSession driveSession) { this.driveSession = driveSession; }

    public String getEmotionName() { return emotionName; }
    public void setEmotionName(String emotionName) { this.emotionName = emotionName; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getPlaylistId() { return playlistId; }
    public void setPlaylistId(String playlistId) { this.playlistId = playlistId; }

    public String getPlaylistName() { return playlistName; }
    public void setPlaylistName(String playlistName) { this.playlistName = playlistName; }

    public String getPlaylistUrl() { return playlistUrl; }
    public void setPlaylistUrl(String playlistUrl) { this.playlistUrl = playlistUrl; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
