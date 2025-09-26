package com.example.demo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    private String id;   // MongoDB ObjectId

    private String name;        // Project name
    private String url;         // Project website URL
    private String description; // Project description

    private List<String> imageUrls; // Multiple Cloudinary image URLs
    private List<String> videoUrls; // Multiple Cloudinary video URLs
}
