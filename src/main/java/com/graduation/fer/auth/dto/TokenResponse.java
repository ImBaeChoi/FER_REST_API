package com.graduation.fer.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
