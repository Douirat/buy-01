package com.zone01.buy01.media_service.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.*;


@Document(collection = "medias")
@Data @AllArgsConstructor @NoArgsConstructor
public class Media {
    @Id
    private String id;
    @Field("image_path")
    private String imagePath;
    @Field("product_id")
    private String productId;
}
