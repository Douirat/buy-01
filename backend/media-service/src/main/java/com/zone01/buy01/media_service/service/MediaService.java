package com.zone01.buy01.media_service.service;

import org.springframework.stereotype.Service;

import com.zone01.buy01.media_service.entities.Media;
import com.zone01.buy01.media_service.entities.OwnerType;
import com.zone01.buy01.media_service.repository.MediaRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

 public Media uploadImage(
        MultipartFile file,
        String ownerId,
        String ownerType
) {

    OwnerType type = ownerType == "USER" ? OwnerType.USER : OwnerType.PRODUCT;

    Media media = Media.builder()
            .path(file.getOriginalFilename()) // temporary
            .ownerId(ownerId)
            .ownerType(type)
            .originalFilename(file.getOriginalFilename())
            .contentType(file.getContentType())
            .build();

    return mediaRepository.save(media);
}
}
