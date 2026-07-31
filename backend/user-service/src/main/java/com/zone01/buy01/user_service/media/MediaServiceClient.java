package com.zone01.buy01.user_service.media;

import com.zone01.buy01.user_service.DTOs.media.MediaResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "media-service", configuration = FeignMultipartConfig.class)
public interface MediaServiceClient {

    @PostMapping(value = "/media/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    MediaResponseDTO uploadImage(
            @RequestPart("file") MultipartFile file,
            @RequestParam("ownerId") String ownerId,
            @RequestParam("ownerType") String ownerType
    );
}