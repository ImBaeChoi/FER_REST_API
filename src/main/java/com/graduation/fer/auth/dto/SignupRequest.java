package com.graduation.fer.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(

        @NotBlank
        @Size(max = 100)
        String userId,

        @NotBlank
        @Size(min = 4)
        String password,

        @NotBlank
        @Size(max = 100)
        String name
) {}
