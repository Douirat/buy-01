package com.zone01.buy01.media_service.event;

public record MediaUploadedEvent(
    String mediaId,
    String ownerId,
    String filename,
    String contentType,
    String uploadedAt
) {

}
