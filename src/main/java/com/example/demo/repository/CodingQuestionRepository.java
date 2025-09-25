package com.example.demo.repository;

import com.example.demo.model.CodingQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodingQuestionRepository extends MongoRepository<CodingQuestion, String> {
}
