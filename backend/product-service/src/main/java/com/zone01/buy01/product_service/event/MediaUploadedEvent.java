package com.zone01.buy01.product_service.event;

import java.time.Instant;

public record MediaUploadedEvent(
    String mediaId,
    String ownerId,
    String filename,
    String contentType,
    Instant uploadedAt
) {

}
