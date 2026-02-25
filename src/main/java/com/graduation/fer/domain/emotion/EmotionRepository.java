package com.graduation.fer.domain.emotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {

    long countByDriveSession_DriveId(Long driveId);

    // ⚠️ EmotionType의 "이름" 필드명이 프로젝트마다 다릅니다.
    // 아래 e.emotionType.name 부분은 실제 필드명으로 맞춰주세요. (예: code, label, typeName 등)
    @Query("""
        select e.emotionType.emotionTypeId, e.emotionType.name, count(e)
        from Emotion e
        where e.driveSession.driveId = :driveId
        group by e.emotionType.emotionTypeId, e.emotionType.name
        order by count(e) desc
    """)
    List<Object[]> countByEmotionType(@Param("driveId") Long driveId);
}
