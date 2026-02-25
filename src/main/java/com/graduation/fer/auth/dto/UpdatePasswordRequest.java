package com.graduation.fer.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @NotBlank String currentPassword,

        @NotBlank
        @Size(min = 4)
        String newPassword
) {}
