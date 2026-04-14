package com.graduation.fer.api.drive.dto;

import com.graduation.fer.domain.music.MusicRecommendation;

import java.time.LocalDateTime;

public record MusicRecommendationResponse(
        Long recommendationId,
        String emotionName,
        String genre,
        String playlistId,
        String playlistName,
        String playlistUrl,
        LocalDateTime issuedAt
) {
    public static MusicRecommendationResponse from(MusicRecommendation rec) {
        return new MusicRecommendationResponse(
                rec.getRecommendationId(),
                rec.getEmotionName(),
                rec.getGenre(),
                rec.getPlaylistId(),
                rec.getPlaylistName(),
                rec.getPlaylistUrl(),
                rec.getIssuedAt()
        );
    }
}
