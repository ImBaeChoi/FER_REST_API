package com.graduation.fer.coaching;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graduation.fer.domain.emotion.CoachingEvent;
import com.graduation.fer.domain.emotion.CoachingEventRepository;
import com.graduation.fer.domain.emotion.Emotion;
import com.graduation.fer.domain.emotion.EmotionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CoachingService {

    private static final Logger log = LoggerFactory.getLogger(CoachingService.class);

    // 위험 감정 목록 - 운전 안전에 영향을 줄 수 있는 감정만 코칭 생성
    private static final Set<String> COACHING_EMOTIONS =
            Set.of("angry", "anger", "fear", "sad", "sadness", "disgust", "surprise", "contempt");

    private final CoachingEventRepository coachingEventRepository;
    private final EmotionRepository emotionRepository;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    public CoachingService(
            CoachingEventRepository coachingEventRepository,
            EmotionRepository emotionRepository,
            ObjectMapper objectMapper
    ) {
        this.coachingEventRepository = coachingEventRepository;
        this.emotionRepository = emotionRepository;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder().baseUrl("https://api.openai.com").build();
    }

    /**
     * 감정 ID를 받아 GPT-4로 코칭 메시지를 생성하고 DB에 저장한다.
     * @Async: 메인 트랜잭션 커밋 이후 비동기로 실행
     * @Transactional: 별도 트랜잭션으로 Emotion 조회 및 CoachingEvent 저장
     */
    @Async
    @Transactional
    public void generateCoaching(Long emotionId) {
        Emotion emotion = emotionRepository.findById(emotionId).orElse(null);
        if (emotion == null) return;

        String emotionName = emotion.getEmotionType().getName().toLowerCase();
        if (!COACHING_EMOTIONS.contains(emotionName)) return;

        try {
            String message = callOpenAi(emotionName, emotion.getConfidence().doubleValue());

            CoachingEvent event = new CoachingEvent();
            event.setUser(emotion.getUser());
            event.setEmotion(emotion);
            event.setType(resolveType(emotionName));
            event.setMessage(message);
            event.setChannel("APP");
            LocalDateTime now = LocalDateTime.now();
            event.setIssuedAt(now);
            event.setCreatedAt(now);

            coachingEventRepository.save(event);
            log.info("Coaching event created for emotionId={}, type={}", emotionId, event.getType());

        } catch (Exception e) {
            log.error("Failed to generate coaching for emotionId={}", emotionId, e);
        }
    }

    private String callOpenAi(String emotionName, double confidence) {
        String systemPrompt = """
                You are a driving safety assistant.
                Provide brief, calm, and practical coaching messages for drivers who show emotional states that could affect driving safety.
                Keep responses under 50 words. Be empathetic and focused on safety.
                Always respond in the same language the user's prompt is written in.
                """;

        String userPrompt = String.format(
                "A driver is showing '%s' emotion with %.0f%% confidence while driving. " +
                "Give a concise safety tip or recommendation.",
                emotionName, confidence * 100
        );

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "max_tokens", 150
        );

        String response = restClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        return extractContent(response);
    }

    private String extractContent(String rawResponse) {
        try {
            JsonNode json = objectMapper.readTree(rawResponse);
            return json.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenAI response: " + rawResponse, e);
        }
    }

    private String resolveType(String emotion) {
        return switch (emotion) {
            case "angry", "anger", "fear", "contempt" -> "Alert";
            case "sad", "sadness", "disgust" -> "Tip";
            default -> "Recommendation";
        };
    }
}
