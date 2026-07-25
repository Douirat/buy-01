package com.zone01.buy01.user_service.DTOs.response.user;

public record UserResponse(
    String id,
    String username,
    String firstName,
    String lastName,
    String email,
    String role
) {}