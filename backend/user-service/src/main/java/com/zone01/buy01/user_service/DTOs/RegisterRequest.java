package com.zone01.buy01.user_service.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.zone01.buy01.user_service.role.*;


public record RegisterRequest(
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    String lastName,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,

    @NotNull(message = "Role is required")
    Role role,

    String avatar // optional — no @NotBlank, may be null or empty from the client
) {}