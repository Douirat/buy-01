package com.zone01.buy01.user_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.zone01.buy01.user_service.module.User;

import java.util.*;



public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByName(String name);

    Optional<User> findByName(String name);

    @Query("{ '$or': [ { 'name': ?0 }, { 'email': ?0 } ] }")
    Optional<User> findByNameOrEmail(String login);
}