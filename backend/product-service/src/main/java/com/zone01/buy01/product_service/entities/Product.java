package com.zone01.buy01.product_service.entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    
    @Id
    private String id;
    @Field("full_name")
    private String name;
    private String description;
    private double price;
    private int quantity;
    @Field("image_urls")
    private List<String> imageUrls;
    @Field("user_id")
    private String userId;
}
