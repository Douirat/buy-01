package com.zone01.buy01.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
public class ProductDto {
    private String id;
    @NotBlank(message = "Product name is required")
    private String name;
    private String description;
    @NotBlank(message = "Product price is required")
    private double price;
    @NotBlank(message = "Product quantity is required")
    private int quantity;
    private String imageUrl;
}
