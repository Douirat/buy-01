package com.zone01.buy01.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zone01.buy01.user_service.DTOs.response.ResponseDTO;
import com.zone01.buy01.user_service.DTOs.response.user.AuthenticatedResponse;
import com.zone01.buy01.user_service.DTOs.response.user.UserLoginRequest;
import com.zone01.buy01.user_service.DTOs.response.user.UserRegisterRequest;
import com.zone01.buy01.user_service.DTOs.response.user.UserResponse;
import com.zone01.buy01.user_service.DTOs.response.user.UserUpdateRequest;
import com.zone01.buy01.user_service.exceptions.*;
import com.zone01.buy01.user_service.jwt.JwtService;
import com.zone01.buy01.user_service.module.User;
import com.zone01.buy01.user_service.repository.UserRepository;
import com.zone01.buy01.user_service.role.Role;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public ResponseDTO<UserResponse> createUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw DuplicateResourceException.emailAlreadyInUse(request.email());
        }

        User user = User.builder()
                .username(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(userRepository.count() == 0 ? Role.CLIENT : Role.SELLER)
                .build();

        User savedUser = userRepository.save(user);

        return ResponseDTO.success(
                "User created successfully.",
                new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail(),
                        savedUser.getRole().name()));
    }

    @Override
    public ResponseDTO<AuthenticatedResponse> loginUser(UserLoginRequest loginRequest) {
        User user = userRepository.findByNameOrEmail(loginRequest.identifier())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getId(), user.getRole().name());
        UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getRole().name());

        return ResponseDTO.success("Login successful.", new AuthenticatedResponse(token, "Bearer", userResponse));
    }

    // UserServiceImpl.java — add
    @Override
    public ResponseDTO<UserResponse> updateUser(String id, UserUpdateRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.user(id));

        existing.setUsername(request.username());
        existing.setEmail(request.email());
        // password and role intentionally untouched here — this is a profile
        // edit endpoint, not a password-reset or role-grant endpoint

        User saved = userRepository.save(existing);
        return ResponseDTO.success("User updated",
                new UserResponse(saved.getId(), saved.getUsername(), saved.getFirstName(), saved.getLastName(), saved.getEmail(), saved.getRole().name()));
    }
}