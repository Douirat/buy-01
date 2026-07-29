package com.zone01.buy01.media_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.zone01.buy01.media_service.entities.Media;

@Repository
public interface MediaRepository extends MongoRepository<Media, String> {

}
