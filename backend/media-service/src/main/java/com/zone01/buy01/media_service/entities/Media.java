package com.zone01.buy01.media_service.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


import lombok.*;

@Document(collection = "media")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media {

    @Id
    private String id;

    @Field("path")
    private String path;

    @Field("owner_id")
    private String ownerId;

    @Field("owner_type")
    private OwnerType ownerType;

    @Field("content_type")
    private String contentType;

    @Field("original_filename")
    private String originalFilename;
}