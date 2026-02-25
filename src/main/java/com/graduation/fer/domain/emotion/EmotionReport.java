package com.graduation.fer.domain.emotion;

import com.graduation.fer.domain.drive.DriveSession;
import com.graduation.fer.domain.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"EmotionReports\"")
public class EmotionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ReportId\"", nullable = false)
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"UserId\"", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"DriveId\"")
    private DriveSession driveSession;

    @Column(name = "\"WindowStart\"", nullable = false)
    private LocalDateTime windowStart;

    @Column(name = "\"WindowEnd\"", nullable = false)
    private LocalDateTime windowEnd;

    @Column(name = "\"SpecVersion\"", length = 50, nullable = false)
    private String specVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "\"Summary\"", nullable = false, columnDefinition = "jsonb")
    private JsonNode summary;

    @Column(name = "\"GeneratedAt\"", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "\"SourceHash\"", length = 64)
    private String sourceHash;

    public Long getReportId() { return reportId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public DriveSession getDriveSession() { return driveSession; }
    public void setDriveSession(DriveSession driveSession) { this.driveSession = driveSession; }

    public LocalDateTime getWindowStart() { return windowStart; }
    public void setWindowStart(LocalDateTime windowStart) { this.windowStart = windowStart; }

    public LocalDateTime getWindowEnd() { return windowEnd; }
    public void setWindowEnd(LocalDateTime windowEnd) { this.windowEnd = windowEnd; }

    public String getSpecVersion() { return specVersion; }
    public void setSpecVersion(String specVersion) { this.specVersion = specVersion; }

    public JsonNode getSummary() { return summary; }
    public void setSummary(JsonNode summary) { this.summary = summary; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public String getSourceHash() { return sourceHash; }
    public void setSourceHash(String sourceHash) { this.sourceHash = sourceHash; }
}
