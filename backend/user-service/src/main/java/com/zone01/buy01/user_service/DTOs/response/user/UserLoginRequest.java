package com.zone01.buy01.user_service.DTOs.response.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
        @NotBlank(message = "Email or username is required")
        String identifier,

        @NotBlank(message = "Password is required")
        String password
) {}