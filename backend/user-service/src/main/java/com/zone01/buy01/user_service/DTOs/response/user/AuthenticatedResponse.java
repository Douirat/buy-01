package com.zone01.buy01.user_service.DTOs.response.user;

public record AuthenticatedResponse(
    String token,
    String type,
    UserResponse user
) {}