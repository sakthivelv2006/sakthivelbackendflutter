package com.example.demo.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.dto.CommentRequest;
import com.example.demo.enumclassess.Filetype;
import com.example.demo.model.FileModel;
import com.example.demo.model.User;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

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

   
@PostMapping(value = "/upload", consumes = {"multipart/form-data"})
public ResponseEntity<?> uploadFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam("userId") String userId, // MongoDB ID
        @RequestParam(value = "description", required = false) String description,
        @RequestParam(value = "type", required = false) String type
) {
    if (file.isEmpty()) {
        return ResponseEntity.badRequest().body("File is missing");
    }

    try {
        Cloudinary cloudinary = getCloudinary();

        Map<String, Object> uploadResult;

        // ✅ Use uploadLarge for big files (>20MB), else normal upload
        if (file.getSize() > 20 * 1024 * 1024) {
            uploadResult = cloudinary.uploader().uploadLarge(
                    file.getInputStream(),
                    ObjectUtils.asMap("resource_type", "auto")
            );
        } else {
            uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto")
            );
        }

        // ✅ Save metadata
        FileModel fileModel = new FileModel();
        fileModel.setAssetId((String) uploadResult.get("asset_id"));
        fileModel.setPublicId((String) uploadResult.get("public_id"));
        fileModel.setOriginalFilename((String) uploadResult.get("original_filename"));
        fileModel.setFormat((String) uploadResult.get("format"));
        fileModel.setResourceType((String) uploadResult.get("resource_type")); // auto = image or video
        fileModel.setUrl((String) uploadResult.get("url"));
        fileModel.setSecureUrl((String) uploadResult.get("secure_url"));
        fileModel.setBytes(((Number) uploadResult.get("bytes")).longValue());
        fileModel.setWidth(uploadResult.get("width") != null ? ((Number) uploadResult.get("width")).intValue() : 0);
        fileModel.setHeight(uploadResult.get("height") != null ? ((Number) uploadResult.get("height")).intValue() : 0);
        fileModel.setSignature((String) uploadResult.get("signature"));
        fileModel.setDescription(description);
        fileModel.setLikes(0);
        fileModel.setUserId(userId);

        if (type != null) {
            fileModel.setType(Filetype.valueOf(type));
        }

        FileModel savedFile = fileRepository.save(fileModel);
        return ResponseEntity.ok(savedFile);

    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
    }
}



  // Get file by ID
    @GetMapping("/{fileId}")
    public ResponseEntity<?> getFileById(@PathVariable String fileId) {
        return fileRepository.findById(fileId)
                .map(ResponseEntity::ok)
                .orElseThrow();
    }

    // Add comment (PATCH)
    @PatchMapping("/{fileId}/comment")
    public ResponseEntity<?> addComment(
            @PathVariable String fileId,
            @RequestBody CommentRequest request
    ) {
        FileModel file = fileRepository.findById(fileId).orElse(null);
        if (file == null) return ResponseEntity.status(404).body(Map.of("error", "File not found"));

        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        String existingComments = file.getComments() == null ? "" : file.getComments();
        file.setComments(existingComments + user.getName() + ": " + request.getComment() + "\n");

        FileModel updatedFile = fileRepository.save(file);
        return ResponseEntity.ok(updatedFile);
    }

    // Increase like count
    @PatchMapping("/{fileId}/like")
    public ResponseEntity<?> increaseLike(@PathVariable String fileId) {
        FileModel file = fileRepository.findById(fileId).orElse(null);
        if (file == null) return ResponseEntity.status(404).body(Map.of("error", "File not found"));

        file.setLikes(file.getLikes() == null ? 1 : file.getLikes() + 1);
        FileModel updatedFile = fileRepository.save(file);
        return ResponseEntity.ok(Map.of(
                "likes", updatedFile.getLikes(),
                "fileId", updatedFile.getId()
        ));
    }

    // Get likes count
    @GetMapping("/{fileId}/likes")
    public ResponseEntity<?> getLikes(@PathVariable String fileId) {
        return fileRepository.findById(fileId)
                .map(file -> ResponseEntity.ok(Map.of(
                        "likes", file.getLikes(),
                        "fileId", file.getId()
                )))
                .orElse(ResponseEntity.status(404).body(Map.of("error", "File not found")));
    }

    // Get all comments
    @GetMapping("/{fileId}/comments")
    public ResponseEntity<?> getComments(@PathVariable String fileId) {
        return fileRepository.findById(fileId)
                .map(file -> ResponseEntity.ok(Map.of(
                        "comments", file.getComments(),
                        "fileId", file.getId()
                )))
                .orElse(ResponseEntity.status(404).body(Map.of("error", "File not found")));
    }

    // Increase like count
    @PutMapping("/{fileId}/like")
    public ResponseEntity<?> increaseLike(@PathVariable String fileId, @RequestParam String userId) {
        FileModel file = fileRepository.findById(fileId).orElse(null);
        if (file == null) return ResponseEntity.badRequest().body("File not found");

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body("Invalid user");

        file.setLikes(file.getLikes() == null ? 1 : file.getLikes() + 1);
        FileModel updatedFile = fileRepository.save(file);
        return ResponseEntity.ok(updatedFile);
    }

    // Get all files
    @GetMapping("/all")
    public ResponseEntity<List<FileModel>> getAllFiles() {
        return ResponseEntity.ok(fileRepository.findAll());
    }

    // Admin: get all files from Cloudinary
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllFilesFromCloudinary() {
        try {
            Cloudinary cloudinary = getCloudinary();
            List<Map<String, Object>> allResources = new ArrayList<>();
            String nextCursor = null;

            do {
                Map<String, Object> result = cloudinary.api().resources(ObjectUtils.asMap(
                        "type", "upload",
                        "resource_type", "auto",
                        "max_results", 500,
                        "next_cursor", nextCursor
                ));
                List<Map<String, Object>> resources = (List<Map<String, Object>>) result.get("resources");
                if (resources != null) allResources.addAll(resources);
                nextCursor = (String) result.get("next_cursor");
            } while (nextCursor != null);

            Map<String, Object> response = Map.of(
                    "total_files", allResources.size(),
                    "files", allResources
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Admin: stats
    @GetMapping("/admin/stats")
    public ResponseEntity<?> getAllFilesStatsFromCloudinary() {
        try {
            Cloudinary cloudinary = getCloudinary();
            int totalFiles = 0;
            long totalBytes = 0;
            String nextCursor = null;

            // Images
            do {
                Map<String, Object> result = cloudinary.api().resources(ObjectUtils.asMap(
                        "type", "upload",
                        "resource_type", "image",
                        "max_results", 100,
                        "next_cursor", nextCursor
                ));
                List<Map<String, Object>> resources = (List<Map<String, Object>>) result.get("resources");
                if (resources != null) {
                    totalFiles += resources.size();
                    for (Map<String, Object> res : resources) {
                        Object bytesObj = res.get("bytes");
                        if (bytesObj instanceof Number) totalBytes += ((Number) bytesObj).longValue();
                    }
                }
                nextCursor = (String) result.get("next_cursor");
            } while (nextCursor != null);

            // Videos
            nextCursor = null;
            do {
                Map<String, Object> result = cloudinary.api().resources(ObjectUtils.asMap(
                        "type", "upload",
                        "resource_type", "video",
                        "max_results", 100,
                        "next_cursor", nextCursor
                ));
                List<Map<String, Object>> resources = (List<Map<String, Object>>) result.get("resources");
                if (resources != null) {
                    totalFiles += resources.size();
                    for (Map<String, Object> res : resources) {
                        Object bytesObj = res.get("bytes");
                        if (bytesObj instanceof Number) totalBytes += ((Number) bytesObj).longValue();
                    }
                }
                nextCursor = (String) result.get("next_cursor");
            } while (nextCursor != null);

            return ResponseEntity.ok(Map.of(
                    "total_files", totalFiles,
                    "total_bytes", totalBytes
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
