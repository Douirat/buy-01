package com.zone01.buy01.user_service.service;

import com.zone01.buy01.user_service.DTOs.response.ResponseDTO;
import com.zone01.buy01.user_service.DTOs.response.user.AuthenticatedResponse;
import com.zone01.buy01.user_service.DTOs.response.user.UserLoginRequest;
import com.zone01.buy01.user_service.DTOs.response.user.UserRegisterRequest;
import com.zone01.buy01.user_service.DTOs.response.user.UserResponse;
import com.zone01.buy01.user_service.DTOs.response.user.UserUpdateRequest;

// UserService.java
public interface UserService {
    ResponseDTO<UserResponse> createUser(UserRegisterRequest request);

    ResponseDTO<AuthenticatedResponse> loginUser(UserLoginRequest loginRequest);

    // UserService.java — add to the interface
    ResponseDTO<UserResponse> updateUser(String id, UserUpdateRequest request);
}