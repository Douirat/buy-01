package com.zone01.buy01.user_service.controller;


import org.springframework.http.MediaType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zone01.buy01.user_service.DTOs.response.ResponseDTO;
import com.zone01.buy01.user_service.DTOs.response.user.AuthenticatedResponse;
import com.zone01.buy01.user_service.DTOs.response.user.UserRegisterRequest;
import com.zone01.buy01.user_service.service.UserService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users/")
public class UserController {

    private final UserService userService;

@PostMapping(
    value = "/register",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public ResponseEntity<ResponseDTO<AuthenticatedResponse>> createUser(
        UserRegisterRequest request) {
System.out.println("fuck you all --------> here");
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(userService.createUser(request));
}

    // // UserManagementController.java — add
    // @PreAuthorize("hasRole('ADMIN')")
    // @PutMapping("/{id}")
    // public ResponseEntity<ResponseDTO<UserResponse>> updateUser(
    // @PathVariable String id,
    // @Valid @ModelAttribute UserUpdateRequest request) {
    // return ResponseEntity.ok(userService.updateUser(id, request));
    // }
}