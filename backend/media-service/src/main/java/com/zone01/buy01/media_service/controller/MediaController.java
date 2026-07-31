package com.zone01.buy01.media_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zone01.buy01.media_service.entities.Media;
import com.zone01.buy01.media_service.entities.OwnerType;
import com.zone01.buy01.media_service.service.MediaService;

@RestController
@RequestMapping("/media/images")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Media> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("ownerId") String ownerId,
            @RequestParam("ownerType") OwnerType ownerType
    ) {

        Media media = mediaService.uploadImage(file, ownerId, ownerType);

        return ResponseEntity.ok(media);
    }
}
