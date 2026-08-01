package com.zone01.buy01.product_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private String id;

    @NotBlank(message = "Product name is required")
    private String name;
    private String description;
    
    @Positive(message = "Product price must be greater than zero")
    private double price;

    @Min(value = 0, message = "Product quantity must be zero or greater")
    private int quantity;

    @NotEmpty(message = "At least one image URL is required")
    private List<@NotBlank(message = "Image URL must not be blank") String> imageUrls;
}
