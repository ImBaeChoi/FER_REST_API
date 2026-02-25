package com.graduation.fer.api.drive.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.List;

public class DriveDetailResponse {

    private Long driveId;
    private Long deviceId;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Long durationSec;

    private long emotionCount;
    private long reportCount;

    private List<EmotionStat> emotionDistribution;

    private LatestReport latestReport; // 없으면 null

    public DriveDetailResponse(
            Long driveId, Long deviceId,
            LocalDateTime startedAt, LocalDateTime endedAt, Long durationSec,
            long emotionCount, long reportCount,
            List<EmotionStat> emotionDistribution,
            LatestReport latestReport
    ) {
        this.driveId = driveId;
        this.deviceId = deviceId;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.durationSec = durationSec;
        this.emotionCount = emotionCount;
        this.reportCount = reportCount;
        this.emotionDistribution = emotionDistribution;
        this.latestReport = latestReport;
    }

    public static class EmotionStat {
        private Long emotionTypeId;
        private String emotionName;
        private long count;
        private double ratio;

        public EmotionStat(Long emotionTypeId, String emotionName, long count, double ratio) {
            this.emotionTypeId = emotionTypeId;
            this.emotionName = emotionName;
            this.count = count;
            this.ratio = ratio;
        }

        public Long getEmotionTypeId() { return emotionTypeId; }
        public String getEmotionName() { return emotionName; }
        public long getCount() { return count; }
        public double getRatio() { return ratio; }
    }

    public static class LatestReport {
        private Long reportId;
        private LocalDateTime windowStart;
        private LocalDateTime windowEnd;
        private LocalDateTime generatedAt;
        private JsonNode summary;

        public LatestReport(Long reportId, LocalDateTime windowStart, LocalDateTime windowEnd,
                            LocalDateTime generatedAt, JsonNode summary) {
            this.reportId = reportId;
            this.windowStart = windowStart;
            this.windowEnd = windowEnd;
            this.generatedAt = generatedAt;
            this.summary = summary;
        }

        public Long getReportId() { return reportId; }
        public LocalDateTime getWindowStart() { return windowStart; }
        public LocalDateTime getWindowEnd() { return windowEnd; }
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public JsonNode getSummary() { return summary; }
    }

    public Long getDriveId() { return driveId; }
    public Long getDeviceId() { return deviceId; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public Long getDurationSec() { return durationSec; }
    public long getEmotionCount() { return emotionCount; }
    public long getReportCount() { return reportCount; }
    public List<EmotionStat> getEmotionDistribution() { return emotionDistribution; }
    public LatestReport getLatestReport() { return latestReport; }
}
