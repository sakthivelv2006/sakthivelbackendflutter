package com.example.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "coding_questions")
public class CodingQuestion {

    @Id
    private String id;

    private String questionName;
    private String questionLevel; // EASY, MEDIUM, HARD
    private String questionType;  // e.g. Array, String, DP

    // Optional links
    private String leetcodeLink;   // can be null
    private String hackerrankLink; // can be null
    private String gfgLink;        // optional

    // Answer fields (directly stored in same doc)
    private String language;   // e.g. "Java"
    private String answerCode; // the actual solution
}
