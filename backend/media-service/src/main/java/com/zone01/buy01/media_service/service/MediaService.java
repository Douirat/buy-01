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
import com.zone01.buy01.media_service.event.MediaEventPublisher;
import com.zone01.buy01.media_service.event.MediaUploadedEvent;
import com.zone01.buy01.media_service.repository.MediaRepository;

@Service
public class MediaService {

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp");

    private static final long MAX_FILE_SIZE_BYTES = 2 * 1024 * 1024;

    private final MediaRepository mediaRepository;
    private final Path storageLocation;
    private final MediaEventPublisher publisher;

    public MediaService(MediaRepository mediaRepository,
            @Value("${media.storage.location:uploads/images}") String storageLocation,
            MediaEventPublisher publisher) {
        this.mediaRepository = mediaRepository;
        this.storageLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
        this.publisher = publisher;

        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create storage directory", e);
        }
    }

    public Media uploadImage(MultipartFile file, String ownerId) {
        validateFile(file);
        String mediaId = UUID.randomUUID().toString();
        Media media = new Media();
        media.setId(mediaId);
        media.setOwnerId(ownerId);
        // media.setOwnerType(OwnerType.PRODUCT);
        media.setFilename(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
        media.setContentType(file.getContentType());
        media.setImagePath(mediaId + "_" + media.getFilename());
        mediaRepository.save(media);

        MediaUploadedEvent event = new MediaUploadedEvent(
                mediaId,
                ownerId,
                media.getFilename(),
                media.getContentType(),
                java.time.Instant.now()
        );
        publisher.publish(event);
        return new Media(media.getId(), media.getOwnerId(), media.getFilename(), media.getContentType(), media.getImagePath());
    }

    public Media getMediaById(String id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found"));
    }

    public Resource loadAsResource(Media media) {
        try {
            Path file = storageLocation.resolve(media.getImagePath()).normalize();
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

        Path file = storageLocation.resolve(existing.getImagePath()).normalize();
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete stored media file",
                    e);
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
