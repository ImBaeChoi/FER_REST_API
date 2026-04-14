package com.graduation.fer.window;

import com.graduation.fer.domain.emotion.Emotion;
import com.graduation.fer.domain.emotion.EmotionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WindowControlService {

    private static final Logger log = LoggerFactory.getLogger(WindowControlService.class);

    private final EmotionRepository emotionRepository;

    public WindowControlService(EmotionRepository emotionRepository) {
        this.emotionRepository = emotionRepository;
    }

    /**
     * 감정 ID를 받아 졸음(drowsy)인 경우 창문을 연다.
     * 배치 업로드 트랜잭션 커밋 후 비동기로 실행.
     */
    @Async
    @Transactional
    public void openWindowIfDrowsy(Long emotionId) {
        Emotion emotion = emotionRepository.findById(emotionId).orElse(null);
        if (emotion == null) return;

        String emotionName = emotion.getEmotionType().getName().toLowerCase();
        if (!emotionName.equals("drowsy")) return;

        openWindow();
    }

    /**
     * 창문 열기 실행.
     * 현재는 로그 출력만 수행하며, 추후 외부 시스템(차량 제어 프로그램 등)과 연동 시
     * 이 메서드 내부에 연동 로직을 추가하면 됩니다.
     */
    public void openWindow() {
        log.info("창문을 엽니다");
        // TODO: 외부 시스템 연동 (예: 차량 제어 프로그램 API 호출)
    }
}
