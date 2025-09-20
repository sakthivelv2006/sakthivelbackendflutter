package com.example.demo.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.model.FileModel;
import com.example.demo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileRepository fileRepository;

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

    // Upload any file (image/video)
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("File is missing");

        try {
            Cloudinary cloudinary = getCloudinary();
            String type = file.getContentType().startsWith("video") ? "video" : "image";

            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", type)
            );

            FileModel fileModel = new FileModel();
            fileModel.setAssetId((String) uploadResult.get("asset_id"));
            fileModel.setPublicId((String) uploadResult.get("public_id"));
            fileModel.setOriginalFilename((String) uploadResult.get("original_filename"));
            fileModel.setFormat((String) uploadResult.get("format"));
            fileModel.setResourceType((String) uploadResult.get("resource_type"));
            fileModel.setUrl((String) uploadResult.get("url"));
            fileModel.setSecureUrl((String) uploadResult.get("secure_url"));
            fileModel.setBytes(((Number) uploadResult.get("bytes")).longValue());
            fileModel.setWidth(uploadResult.get("width") != null ? ((Number) uploadResult.get("width")).intValue() : 0);
            fileModel.setHeight(uploadResult.get("height") != null ? ((Number) uploadResult.get("height")).intValue() : 0);
            fileModel.setSignature((String) uploadResult.get("signature"));

            FileModel savedFile = fileRepository.save(fileModel);
            return ResponseEntity.ok(savedFile);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }



    // Get all uploaded files
    @GetMapping("/all")
    public ResponseEntity<List<FileModel>> getAllFiles() {
        return ResponseEntity.ok(fileRepository.findAll());
    }









@GetMapping("/admin/all")
public ResponseEntity<?> getAllFilesFromCloudinary() {
    try {
        Cloudinary cloudinary = getCloudinary();

        // Pagination support
        List<Map<String, Object>> allResources = new ArrayList<>();
        String nextCursor = null;

        do {
            Map<String, Object> result = cloudinary.api().resources(ObjectUtils.asMap(
                    "type", "upload",
                    "resource_type", "auto",
                    "max_results", 500,
                    "next_cursor", nextCursor
            ));

            // Extract resources
            List<Map<String, Object>> resources = (List<Map<String, Object>>) result.get("resources");
            if (resources != null) {
                allResources.addAll(resources);
            }

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

@GetMapping("/admin/stats")
public ResponseEntity<?> getAllFilesStatsFromCloudinary() {
    try {
        Cloudinary cloudinary = getCloudinary();

        int totalFiles = 0;
        long totalBytes = 0;
        String nextCursor = null;

        // Handle images
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

        // Handle videos
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

