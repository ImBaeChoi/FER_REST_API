package com.graduation.fer.music;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graduation.fer.domain.emotion.Emotion;
import com.graduation.fer.domain.emotion.EmotionRepository;
import com.graduation.fer.domain.music.MusicRecommendation;
import com.graduation.fer.domain.music.MusicRecommendationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
public class SpotifyService {

    private static final Logger log = LoggerFactory.getLogger(SpotifyService.class);

    // 감정 → 검색 키워드 매핑 (운전 안전 기반)
    private static final Map<String, String> EMOTION_QUERY_MAP = Map.ofEntries(
            Map.entry("angry",     "calming classical driving music"),
            Map.entry("anger",     "calming classical driving music"),
            Map.entry("fear",      "relaxing ambient chill music"),
            Map.entry("sad",       "uplifting happy pop music"),
            Map.entry("sadness",   "uplifting happy pop music"),
            Map.entry("disgust",   "pleasant acoustic background music"),
            Map.entry("surprise",  "calming lo-fi focus music"),
            Map.entry("contempt",  "peaceful jazz instrumental"),
            Map.entry("happy",     "upbeat pop driving music"),
            Map.entry("happiness", "upbeat pop driving music"),
            Map.entry("neutral",   "lo-fi instrumental background music")
    );

    // 감정 → 장르 레이블
    private static final Map<String, String> EMOTION_GENRE_MAP = Map.ofEntries(
            Map.entry("angry",     "classical"),
            Map.entry("anger",     "classical"),
            Map.entry("fear",      "ambient"),
            Map.entry("sad",       "pop"),
            Map.entry("sadness",   "pop"),
            Map.entry("disgust",   "acoustic"),
            Map.entry("surprise",  "lo-fi"),
            Map.entry("contempt",  "jazz"),
            Map.entry("happy",     "pop"),
            Map.entry("happiness", "pop"),
            Map.entry("neutral",   "lo-fi")
    );

    private final EmotionRepository emotionRepository;
    private final MusicRecommendationRepository musicRecommendationRepository;
    private final ObjectMapper objectMapper;
    private final RestClient spotifyRestClient;
    private final RestClient accountsRestClient;

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    // 토큰 캐시 (만료 전까지 재사용)
    private String cachedToken;
    private Instant tokenExpiresAt = Instant.EPOCH;

    public SpotifyService(
            EmotionRepository emotionRepository,
            MusicRecommendationRepository musicRecommendationRepository,
            ObjectMapper objectMapper
    ) {
        this.emotionRepository = emotionRepository;
        this.musicRecommendationRepository = musicRecommendationRepository;
        this.objectMapper = objectMapper;
        this.spotifyRestClient = RestClient.builder().baseUrl("https://api.spotify.com").build();
        this.accountsRestClient = RestClient.builder().baseUrl("https://accounts.spotify.com").build();
    }

    /**
     * 감정 ID를 받아 Spotify에서 어울리는 플레이리스트를 추천하고 DB에 저장한다.
     * CoachingService와 동일하게 트랜잭션 커밋 후 비동기 실행.
     */
    @Async
    @Transactional
    public void recommendMusic(Long emotionId) {
        Emotion emotion = emotionRepository.findById(emotionId).orElse(null);
        if (emotion == null) return;

        String emotionName = emotion.getEmotionType().getName().toLowerCase();
        String query = EMOTION_QUERY_MAP.getOrDefault(emotionName, "relaxing background music");
        String genre = EMOTION_GENRE_MAP.getOrDefault(emotionName, "ambient");

        try {
            String token = getAccessToken();
            JsonNode playlist = searchPlaylist(token, query);

            MusicRecommendation rec = new MusicRecommendation();
            rec.setUser(emotion.getUser());
            rec.setEmotion(emotion);
            rec.setDriveSession(emotion.getDriveSession());
            rec.setEmotionName(emotionName);
            rec.setGenre(genre);

            if (playlist != null) {
                rec.setPlaylistId(playlist.path("id").asText(null));
                rec.setPlaylistName(playlist.path("name").asText(null));
                rec.setPlaylistUrl(playlist.path("external_urls").path("spotify").asText(null));
            }

            LocalDateTime now = LocalDateTime.now();
            rec.setIssuedAt(now);
            rec.setCreatedAt(now);

            musicRecommendationRepository.save(rec);
            log.info("Music recommendation saved for emotionId={}, genre={}, playlist={}",
                    emotionId, genre, rec.getPlaylistName());

        } catch (Exception e) {
            log.error("Failed to recommend music for emotionId={}", emotionId, e);
        }
    }

    // Spotify Client Credentials 토큰 발급 (만료 60초 전에 갱신)
    private synchronized String getAccessToken() throws Exception {
        if (cachedToken != null && Instant.now().plusSeconds(60).isBefore(tokenExpiresAt)) {
            return cachedToken;
        }

        String credentials = Base64.getEncoder().encodeToString(
                (clientId + ":" + clientSecret).getBytes()
        );

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        String response = accountsRestClient.post()
                .uri("/api/token")
                .header("Authorization", "Basic " + credentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(String.class);

        JsonNode json = objectMapper.readTree(response);
        cachedToken = json.path("access_token").asText();
        int expiresIn = json.path("expires_in").asInt(3600);
        tokenExpiresAt = Instant.now().plusSeconds(expiresIn);

        return cachedToken;
    }

    // 감정 키워드로 Spotify 플레이리스트 검색 (첫 번째 결과 반환)
    private JsonNode searchPlaylist(String token, String query) throws Exception {
        String response = spotifyRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search")
                        .queryParam("q", query)
                        .queryParam("type", "playlist")
                        .queryParam("limit", "1")
                        .queryParam("market", "KR")
                        .build())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(String.class);

        JsonNode json = objectMapper.readTree(response);
        JsonNode items = json.path("playlists").path("items");
        if (items.isArray() && !items.isEmpty()) {
            return items.get(0);
        }
        return null;
    }
}
