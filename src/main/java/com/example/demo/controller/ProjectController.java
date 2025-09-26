
package com.example.demo.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.model.Project;
import com.example.demo.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    private Cloudinary getCloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }



    // ---------------- CREATE PROJECT (IMAGES ONLY) ----------------
    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createProject(
            @RequestParam("name") String name,
            @RequestParam("url") String url,
            @RequestParam("description") String description,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) {
        List<String> imageUrls = new ArrayList<>();
        Cloudinary cloudinary = getCloudinary();

        try {
            // Upload images
            if (images != null) {
                for (MultipartFile img : images) {
                    if (img != null && !img.isEmpty()) {
                        Map<?, ?> result = cloudinary.uploader().upload(
                                img.getBytes(),
                                ObjectUtils.asMap("resource_type", "image")
                        );
                        imageUrls.add((String) result.get("secure_url"));
                    }
                }
            }

            Project project = Project.builder()
                    .name(name)
                    .url(url)
                    .description(description)
                    .imageUrls(imageUrls)
                    .build();

            Project saved = projectRepository.save(project);
            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

   

    // ---------------- GET ALL PROJECTS ----------------
    @GetMapping
    public ResponseEntity<?> getAllProjects() {
        return ResponseEntity.ok(projectRepository.findAll());
    }

    // ---------------- GET PROJECT BY ID ----------------
    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable String id) {
        return projectRepository.findById(id).orElseThrow();
    }

    // ---------------- UPDATE PROJECT ----------------
    @PutMapping(value = "/update/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProject(
            @PathVariable String id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "url", required = false) String url,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "videos", required = false) List<MultipartFile> videos
    ) {
        return projectRepository.findById(id).map(project -> {
            try {
                Cloudinary cloudinary = getCloudinary();

                // Update basic fields
                if (name != null) project.setName(name);
                if (url != null) project.setUrl(url);
                if (description != null) project.setDescription(description);

                // Upload new images if provided
                if (images != null) {
                    List<String> newImageUrls = new ArrayList<>(project.getImageUrls() != null ? project.getImageUrls() : new ArrayList<>());
                    for (MultipartFile img : images) {
                        if (!img.isEmpty()) {
                            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                                    img.getBytes(), ObjectUtils.asMap("resource_type", "image"));
                            newImageUrls.add((String) uploadResult.get("secure_url"));
                        }
                    }
                    project.setImageUrls(newImageUrls);
                }

                // Upload new videos if provided
                if (videos != null) {
                    List<String> newVideoUrls = new ArrayList<>(project.getVideoUrls() != null ? project.getVideoUrls() : new ArrayList<>());
                    for (MultipartFile vid : videos) {
                        if (!vid.isEmpty()) {
                            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                                    vid.getBytes(), ObjectUtils.asMap("resource_type", "video"));
                            newVideoUrls.add((String) uploadResult.get("secure_url"));
                        }
                    }
                    project.setVideoUrls(newVideoUrls);
                }

                Project updated = projectRepository.save(project);
                return ResponseEntity.ok(updated);

            } catch (IOException e) {
                return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
            }
        }).orElse(ResponseEntity.status(404).body("Project not found"));
    }

    // ---------------- DELETE PROJECT ----------------
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable String id) {
        return projectRepository.findById(id).map(project -> {
            projectRepository.delete(project);
            return ResponseEntity.ok("Project deleted successfully");
        }).orElse(ResponseEntity.status(404).body("Project not found"));
    }
}
