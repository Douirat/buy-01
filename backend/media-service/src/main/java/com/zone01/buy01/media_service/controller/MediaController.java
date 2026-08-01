package com.zone01.buy01.media_service.controller;

import java.time.Duration;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zone01.buy01.media_service.entities.Media;
import com.zone01.buy01.media_service.service.MediaService;

import jakarta.annotation.security.PermitAll;

@RestController
@RequestMapping("/media/images")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Media> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("ownerId") String ownerId,
            @RequestParam("ownerType") String ownerType,
            Authentication authentication
    ) {
        Media media = mediaService.uploadImage(file, ownerId, ownerType, authentication.getName());
        return ResponseEntity.ok(media);
    }

    @GetMapping("/{id}")
    @PermitAll
    public ResponseEntity<Resource> getImage(@PathVariable String id) {
        Media media = mediaService.getMediaById(id);
        Resource resource = mediaService.loadAsResource(media);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(media.getContentType()))
                .header(HttpHeaders.CACHE_CONTROL, CacheControl.maxAge(Duration.ofDays(1)).cachePublic().getHeaderValue())
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteImage(@PathVariable String id, Authentication authentication) {
        mediaService.deleteMedia(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
