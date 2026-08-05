package com.zone01.buy01.user_service.service;

import lombok.AllArgsConstructor;

import java.time.Instant;

import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

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
import com.zone01.buy01.user_service.module.User;
import com.zone01.buy01.user_service.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final WebClient.Builder webClientBuilder;

        @Override
        public ResponseDTO<AuthenticatedResponse> createUser(UserRegisterRequest request) {

                System.out.println(request);

                if (userRepository.existsByEmail(request.email()) || userRepository.existsByUsername(request.email())) {
                        throw DuplicateResourceException.emailAlreadyInUse(request.email());
                }

                User user = request.toUser();

                System.out.println(user.toString());

                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setCreatedAt(Instant.now());

                User saved = userRepository.save(user); // saved first — Media Service needs the userId as ownerId

                String token = jwtService.generateToken(saved.getId(), saved.getRole().name());

                // 3. If they submitted an avatar, upload it to Media Service using that token
                if (request.avatar() != null && !request.avatar().isEmpty()) {
                        String mediaId = uploadAvatarToMediaService(user.getId(), request.avatar(), token);
                        user.setAvatar(mediaId); // add this field to your User entity
                        userRepository.save(user);
                }

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

        //TODO: Change this to kafka.
        // send an http request to the media service for user registration:
        private String uploadAvatarToMediaService(String userId, MultipartFile avatar, String token) {
                try {
                        MultipartBodyBuilder builder = new MultipartBodyBuilder();
                        builder.part("file", avatar.getResource());
                        builder.part("ownerId", userId);
                        builder.part("ownerType", "USER");

                        MediaResponseDTO media = webClientBuilder.build()
                                        .post()
                                        .uri("http://media-service/media/images/upload")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .body(BodyInserters.fromMultipartData(builder.build()))
                                        .retrieve()
                                        .bodyToMono(MediaResponseDTO.class)
                                        .block();

                        return media != null ? media.getId() : null;
                } catch (Exception e) {
                        System.out.println(
                                        String.format("Avatar upload failed for user {}: {}", userId, e.getMessage()));
                        return null; // user is registered without an avatar; they can upload one later
                }
        }
}