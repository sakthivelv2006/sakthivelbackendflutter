package com.example.demo.repository;

import com.example.demo.model.FileModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends MongoRepository<FileModel, String> {
    // You can add custom query methods here if needed
}
