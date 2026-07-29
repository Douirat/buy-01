package com.zone01.buy01.user_service.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.zone01.buy01.user_service.DTOs.response.*;
import com.zone01.buy01.user_service.DTOs.response.user.AuthenticatedResponse;
import com.zone01.buy01.user_service.DTOs.response.user.UserLoginRequest;
import com.zone01.buy01.user_service.jwt.JwtService;
import com.zone01.buy01.user_service.repository.UserRepository;
import com.zone01.buy01.user_service.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


/**
 * This is the PRODUCER of JWTs. The gateway you already have only
 * consumes/validates tokens - this is where they actually get minted,
 * because this service is the only one with access to the password hash.
 *
 * Wire this in as-is if your User entity/repository match the field names
 * below, otherwise adjust findByEmail / getPasswordHash / getRole to your
 * existing User model - don't rename your existing fields to match this.
 */
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<AuthenticatedResponse>> loginUser(
             UserLoginRequest loginRequest) {
                System.out.println("11111111111111");
        return ResponseEntity.ok().body(userService.loginUser(loginRequest));
    }

}