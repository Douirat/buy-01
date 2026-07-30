package com.zone01.buy01.product_service.service;

import com.zone01.buy01.product_service.repository.ProductRepository;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zone01.buy01.product_service.entities.Product;
import com.zone01.buy01.product_service.exceptions.ResourceNotFoundException;

import com.zone01.buy01.product_service.dto.ProductDto;


@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProductById(String id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(ProductDto dto, String imageUrl, String userId) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setImageUrl(imageUrl);
        product.setUserId(userId);

        return productRepository.save(product);
    }

    public Product updateProduct(String id, ProductDto dto, String imageUrl, String authenticatedUserEmail) {
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
        
        if (!existingProduct.getUserId().equals(authenticatedUserEmail)) {
            throw new SecurityException("You are not authorized to update this product");
        }

        existingProduct.setName(dto.getName());
        existingProduct.setDescription(dto.getDescription());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setQuantity(dto.getQuantity());
        existingProduct.setImageUrl(imageUrl);
    
        return productRepository.save(existingProduct);
    }

    public void deleteProduct(String id, String authenticatedUserEmail) {
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
        
        if (!existingProduct.getUserId().equals(authenticatedUserEmail)) {
            throw new SecurityException("You are not authorized to delete this product");
        }
        productRepository.deleteById(id);
    }
}