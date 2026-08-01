package com.zone01.buy01.product_service.service;

import com.zone01.buy01.product_service.dto.ProductDto;
import com.zone01.buy01.product_service.entities.Product;
import com.zone01.buy01.product_service.exceptions.ResourceNotFoundException;
import com.zone01.buy01.product_service.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;


@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDto getProductById(String id) {
        return productRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto createProduct(ProductDto dto, String userId) {
        Product product = toEntity(dto);
        product.setUserId(userId);

        Product saved = productRepository.save(product);
        return toDto(saved);
    }

    public ProductDto updateProduct(String id, ProductDto dto, String authenticatedUserEmail) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));

        if (!existingProduct.getUserId().equals(authenticatedUserEmail)) {
            throw new SecurityException("You are not authorized to update this product");
        }

        existingProduct.setName(dto.getName());
        existingProduct.setDescription(dto.getDescription());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setQuantity(dto.getQuantity());
        existingProduct.setImageUrls(dto.getImageUrls());

        return toDto(productRepository.save(existingProduct));
    }

    public void deleteProduct(String id, String authenticatedUserEmail) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));

        if (!existingProduct.getUserId().equals(authenticatedUserEmail)) {
            throw new SecurityException("You are not authorized to delete this product");
        }

        productRepository.deleteById(id);
    }

    private ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .imageUrls(product.getImageUrls())
                .build();
    }

    private Product toEntity(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setImageUrls(dto.getImageUrls());
        return product;
    }
}