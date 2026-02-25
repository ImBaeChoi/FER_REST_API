package com.graduation.fer.api.drive.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class BatchUploadRequest {

    @Size(max = 2000)
    private List<EmotionEventDto> emotions;

    @Size(max = 5000)
    private List<BioSignalDto> biosignals;

    public List<EmotionEventDto> getEmotions() { return emotions; }
    public void setEmotions(List<EmotionEventDto> emotions) { this.emotions = emotions; }

    public List<BioSignalDto> getBiosignals() { return biosignals; }
    public void setBiosignals(List<BioSignalDto> biosignals) { this.biosignals = biosignals; }

    public static class EmotionEventDto {
        @NotNull
        private Long deviceId;

        @NotNull
        private Short emotionTypeId;

        @NotNull
        private String source;          // Vision/Bio/Fusion

        @NotNull
        private String modelVersion;

        @NotNull
        private String occurredAt;      // ISO-8601 string (ex. 2026-01-26T12:34:56)

        @NotNull
        private String confidence;      // "0.778" 등 문자열로 받고 BigDecimal로 변환

        private JsonNode payload;         // JSON string (원본 scores/alert 등)

        public Long getDeviceId() { return deviceId; }
        public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

        public Short getEmotionTypeId() { return emotionTypeId; }
        public void setEmotionTypeId(Short emotionTypeId) { this.emotionTypeId = emotionTypeId; }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }

        public String getModelVersion() { return modelVersion; }
        public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

        public String getOccurredAt() { return occurredAt; }
        public void setOccurredAt(String occurredAt) { this.occurredAt = occurredAt; }

        public String getConfidence() { return confidence; }
        public void setConfidence(String confidence) { this.confidence = confidence; }

        public JsonNode getPayload() { return payload; }
        public void setPayload(JsonNode payload) { this.payload = payload; }
    }

    public static class BioSignalDto {
        @NotNull
        private Long deviceId;

        private String signalType;  // "HRV", "GSR" 등 (DB SignalType 컬럼)
        @NotNull
        private Double value;

        @NotNull
        private String unit;

        @NotNull
        private String measuredAt;  // ISO-8601 string

        private String qualityFlag;
        private JsonNode attr;      // JSON string

        public Long getDeviceId() { return deviceId; }
        public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

        public String getSignalType() { return signalType; }
        public void setSignalType(String signalType) { this.signalType = signalType; }

        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }

        public String getMeasuredAt() { return measuredAt; }
        public void setMeasuredAt(String measuredAt) { this.measuredAt = measuredAt; }

        public String getQualityFlag() { return qualityFlag; }
        public void setQualityFlag(String qualityFlag) { this.qualityFlag = qualityFlag; }

        public JsonNode getAttr() { return attr; }
        public void setAttr(JsonNode attr) { this.attr = attr; }
    }
}
