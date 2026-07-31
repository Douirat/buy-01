package com.zone01.buy01.user_service.DTOs.media;

import lombok.Data;

@Data
public class MediaResponseDTO {
    private String id;
    private String path;
    private String ownerId;
    private String ownerType;
    private String contentType;
    private String originalFilename;
}