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
import org.springframework.web.bind.annotation.RestController;

import com.zone01.buy01.product_service.dto.ProductDto;
import com.zone01.buy01.product_service.service.ProductService;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @PermitAll
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("{id}")
    @PermitAll
    public ProductDto getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ProductDto createProduct(@Valid @RequestBody ProductDto prodDto, Authentication authentication) {
        String userId = authentication.getName();
        return productService.createProduct(prodDto, userId);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ProductDto updateProduct(@PathVariable String id, @Valid @RequestBody ProductDto prodDto,
            Authentication authentication) {
        String username = authentication.getName();
        return productService.updateProduct(id, prodDto, username);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('SELLER')")
    public void deleteProduct(@PathVariable String id, Authentication authentication) {
        String username = authentication.getName();
        productService.deleteProduct(id, username);
    }
}
