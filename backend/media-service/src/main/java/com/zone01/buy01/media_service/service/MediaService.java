package com.zone01.buy01.media_service.service;

import org.springframework.stereotype.Service;

import com.zone01.buy01.media_service.entities.Media;
import com.zone01.buy01.media_service.repository.MediaRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public Media uploadImage(MultipartFile file, String productId) {
        return mediaRepository.save(new Media(null, file.getOriginalFilename(), productId));
    }
    
}
