package com.zone01.buy01.product_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zone01.buy01.product_service.dto.ProductDto;
import com.zone01.buy01.product_service.entities.Product;
import com.zone01.buy01.product_service.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("{id}")
    public Product getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public Product createProduct(@RequestBody ProductDto prodDto) {
        return productService.createProduct(prodDto);
    }

    @PutMapping("{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody ProductDto prodDto) {
        return productService.updateProduct(id, prodDto);
    }

    @DeleteMapping("{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
    }
}
