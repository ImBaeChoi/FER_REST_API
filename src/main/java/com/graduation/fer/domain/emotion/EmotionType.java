package com.graduation.fer.domain.emotion;

import jakarta.persistence.*;

@Entity
@Table(name = "\"EmotionTypes\"")
public class EmotionType {

    @Id
    @Column(name = "\"EmotionTypeId\"", nullable = false)
    private Short emotionTypeId;

    @Column(name = "\"Name\"", length = 20, nullable = false, unique = true)
    private String name;

    public Short getEmotionTypeId() { return emotionTypeId; }
    public void setEmotionTypeId(Short emotionTypeId) { this.emotionTypeId = emotionTypeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
