package com.zone01.buy01.product_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.zone01.buy01.product_service.entities.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    
}
