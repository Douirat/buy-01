package com.zone01.buy01.media_service.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.zone01.buy01.media_service.entities.Media;
import com.zone01.buy01.media_service.entities.OwnerType;
import com.zone01.buy01.media_service.repository.MediaRepository;

@Service
public class MediaService {

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE_BYTES = 2 * 1024 * 1024;

    private final MediaRepository mediaRepository;
    private final Path storageLocation;

    public MediaService(MediaRepository mediaRepository,
                        @Value("${media.storage.location:uploads/images}") String storageLocation) {
        this.mediaRepository = mediaRepository;
        this.storageLocation = Paths.get(storageLocation).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create storage directory", e);
        }
    }

    public Media uploadImage(MultipartFile file, String ownerId, String ownerType) {
        validateFile(file);

        OwnerType type;
        try {
            type = OwnerType.valueOf(ownerType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ownerType must be USER or PRODUCT");
        }

        // if (type == OwnerType.USER && !authenticatedUserId.equals(ownerId)) {
        //     throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Authenticated user does not own this media");
        // }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (originalFilename.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file name");
        }

        String extension = StringUtils.getFilenameExtension(originalFilename);
        String storedFilename = UUID.randomUUID().toString() + (extension != null ? "." + extension : "");
        Path destination = storageLocation.resolve(storedFilename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file", e);
        }

        Media media = Media.builder()
                .path(storedFilename)
                .ownerId(ownerId)
                .ownerType(type)
                .originalFilename(originalFilename)
                .contentType(Objects.requireNonNull(file.getContentType()))
                .build();

        return mediaRepository.save(media);
    }

    public Media getMediaById(String id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found"));
    }

    public Resource loadAsResource(Media media) {
        try {
            Path file = storageLocation.resolve(media.getPath()).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Media file not found");
            }
            return resource;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to load media file", e);
        }
    }

    public void deleteMedia(String id, String authenticatedUserId) {
        Media existing = getMediaById(id);
        if (!existing.getOwnerId().equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Authenticated user does not own this media");
        }

        Path file = storageLocation.resolve(existing.getPath()).normalize();
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete stored media file", e);
        }

        mediaRepository.deleteById(id);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size must be 2MB or less");
        }

        String contentType = file.getContentType();
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unsupported file type. Only JPEG, PNG, GIF, and WEBP are allowed");
        }
    }
}
