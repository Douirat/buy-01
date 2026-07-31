package com.zone01.buy01.user_service.mapper;

import com.zone01.buy01.user_service.DTOs.response.user.UserResponse;
import com.zone01.buy01.user_service.module.User;

public class UserMapper {

    private UserMapper() {
        // utility class — no instances
    }

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null
        );
    }
}