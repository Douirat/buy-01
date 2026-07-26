package com.zone01.buy01.user_service.controller;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zone01.buy01.user_service.DTOs.response.ResponseDTO;
import com.zone01.buy01.user_service.DTOs.response.user.AuthenticatedResponse;
import com.zone01.buy01.user_service.DTOs.response.user.UserLoginRequest;
import com.zone01.buy01.user_service.DTOs.response.user.UserRegisterRequest;
import com.zone01.buy01.user_service.DTOs.response.user.UserResponse;
import com.zone01.buy01.user_service.DTOs.response.user.UserUpdateRequest;
import com.zone01.buy01.user_service.service.UserService;


@RestController
@AllArgsConstructor
@RequestMapping("/api/api/")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<UserResponse>> createUser(@Valid @ModelAttribute UserRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }



    // // UserManagementController.java — add
    // @PreAuthorize("hasRole('ADMIN')")
    // @PutMapping("/{id}")
    // public ResponseEntity<ResponseDTO<UserResponse>> updateUser(
    //         @PathVariable String id,
    //         @Valid @ModelAttribute UserUpdateRequest request) {
    //     return ResponseEntity.ok(userService.updateUser(id, request));
    // }
}