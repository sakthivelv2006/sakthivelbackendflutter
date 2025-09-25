package com.example.demo.controller;

import com.example.demo.model.CodingQuestion;
import com.example.demo.repository.CodingQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coding-questions")
public class CodingQuestionController {

    @Autowired
    private CodingQuestionRepository repository;

    // ✅ Get all questions
    @GetMapping
    public List<CodingQuestion> getAllQuestions() {
        return repository.findAll();
    }

    // ✅ Get question by ID
    @GetMapping("/{id}")
    public ResponseEntity<CodingQuestion> getQuestionById(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Create a new question
    @PostMapping
    public CodingQuestion createQuestion(@RequestBody CodingQuestion question) {
        return repository.save(question);
    }

    // ✅ Update an existing question
    @PutMapping("/{id}")
    public ResponseEntity<CodingQuestion> updateQuestion(
            @PathVariable String id,
            @RequestBody CodingQuestion updatedQuestion) {

        return repository.findById(id).map(existing -> {
            existing.setQuestionName(updatedQuestion.getQuestionName());
            existing.setQuestionLevel(updatedQuestion.getQuestionLevel());
            existing.setQuestionType(updatedQuestion.getQuestionType());

            // optional links
            existing.setLeetcodeLink(updatedQuestion.getLeetcodeLink());
            existing.setHackerrankLink(updatedQuestion.getHackerrankLink());
            existing.setGfgLink(updatedQuestion.getGfgLink());

            existing.setLanguage(updatedQuestion.getLanguage());
            existing.setAnswerCode(updatedQuestion.getAnswerCode());

            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Delete a question
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable String id) {
        return repository.findById(id).map(existing -> {
            repository.delete(existing);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
