package com.zone01.buy01.product_service.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zone01.buy01.product_service.dto.ProductDto;
import com.zone01.buy01.product_service.entities.Product;
import com.zone01.buy01.product_service.service.ProductService;

import jakarta.annotation.security.PermitAll;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
     @PermitAll
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("{id}")
    @PermitAll
    public Product getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() && hasRole('SELLER')")
    public Product createProduct(@RequestBody ProductDto prodDto, @RequestParam String imageUrl, Authentication authentication) {
        String userId = authentication.getName();
        return productService.createProduct(prodDto, imageUrl, userId);
    }

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated() && hasRole('SELLER')")
    public Product updateProduct(@PathVariable String id, @RequestBody ProductDto prodDto,
            Authentication authentication) {
        String username = authentication.getName();
        String imageUrl = prodDto.getImageUrl();
        return productService.updateProduct(id, prodDto, username, imageUrl);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated() && hasRole('SELLER')")
    public void deleteProduct(@PathVariable String id, Authentication authentication) {
        String username = authentication.getName();
        productService.deleteProduct(id, username);
    }
}
