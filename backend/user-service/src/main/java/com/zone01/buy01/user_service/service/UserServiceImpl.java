package com.zone01.buy01.user_service.service;

import lombok.AllArgsConstructor;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zone01.buy01.user_service.DTOs.media.MediaResponseDTO;
import com.zone01.buy01.user_service.DTOs.response.ResponseDTO;
import com.zone01.buy01.user_service.DTOs.response.user.AuthenticatedResponse;
import com.zone01.buy01.user_service.DTOs.response.user.UserLoginRequest;
import com.zone01.buy01.user_service.DTOs.response.user.UserRegisterRequest;
import com.zone01.buy01.user_service.DTOs.response.user.UserResponse;
import com.zone01.buy01.user_service.DTOs.response.user.UserUpdateRequest;
import com.zone01.buy01.user_service.exceptions.*;
import com.zone01.buy01.user_service.jwt.JwtService;
import com.zone01.buy01.user_service.mapper.UserMapper;
import com.zone01.buy01.user_service.media.MediaServiceClient;
import com.zone01.buy01.user_service.module.User;
import com.zone01.buy01.user_service.repository.UserRepository;


@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final MediaServiceClient mediaServiceClient;

        @Override
        public ResponseDTO<AuthenticatedResponse>  createUser(UserRegisterRequest request) {

                System.out.println(request);

                if (userRepository.existsByEmail(request.email()) || userRepository.existsByUsername(request.email())) {
                        throw DuplicateResourceException.emailAlreadyInUse(request.email());
                }



                User user = request.toUser();

                System.out.println(user.toString());

                
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setCreatedAt(Instant.now());
                

                User saved = userRepository.save(user); // saved first — Media Service needs the userId as ownerId

                if (request.avatar() != null && !request.avatar().isEmpty()) {
                        try {
                                MediaResponseDTO media = mediaServiceClient.uploadImage(request.avatar(), saved.getId(),
                                                "USER");
                                saved.setAvatar(media.getPath());
                                saved = userRepository.save(saved);
                        } catch (Exception e) {
                                System.out.println(String.format("Avatar upload failed for user {}: {}", saved.getId(),
                                                e.getMessage()));
                        }
                }




                String token = jwtService.generateToken(saved.getId(), saved.getRole().name());

                AuthenticatedResponse authResponse = new AuthenticatedResponse(
                                token,
                                "Bearer",
                                UserMapper.toResponse(saved));

                return ResponseDTO.success("User registered successfully", authResponse);
        }

        @Override
        public ResponseDTO<AuthenticatedResponse> loginUser(UserLoginRequest loginRequest) {
                System.out.println("login cred: " + loginRequest.identifier() + "   " + loginRequest.password());
                User user = userRepository.findByEmail(loginRequest.identifier())
                                .orElseThrow(InvalidCredentialsException::new);

                System.out.println("---> " + user.toString());

                if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
                        throw new InvalidCredentialsException();
                }

                String token = jwtService.generateToken(user.getId(), user.getRole().name());
                UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getFirstName(),
                                user.getLastName(), user.getEmail(),
                                user.getRole().name());

                return ResponseDTO.success("Login successful.",
                                new AuthenticatedResponse(token, "Bearer", userResponse));
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
                                new UserResponse(saved.getId(), saved.getUsername(), saved.getFirstName(),
                                                saved.getLastName(), saved.getEmail(), saved.getRole().name()));
        }
}