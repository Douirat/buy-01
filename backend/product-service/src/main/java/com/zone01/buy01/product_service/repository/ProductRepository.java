package com.zone01.buy01.product_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.zone01.buy01.product_service.entities.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
}
