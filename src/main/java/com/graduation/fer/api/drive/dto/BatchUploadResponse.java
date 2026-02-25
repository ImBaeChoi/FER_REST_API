package com.graduation.fer.api.drive.dto;

public class BatchUploadResponse {
    private int savedEmotions;
    private int savedBiosignals;

    public BatchUploadResponse(int savedEmotions, int savedBiosignals) {
        this.savedEmotions = savedEmotions;
        this.savedBiosignals = savedBiosignals;
    }

    public int getSavedEmotions() { return savedEmotions; }
    public int getSavedBiosignals() { return savedBiosignals; }
}
