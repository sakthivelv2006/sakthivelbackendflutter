package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reels_videos")
public class ReelsVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // File info
    private String originalFilename;  // Uploaded file name
    private String url;               // File URL (video or image)
    private String format;            // mp4, mov, jpg, png, etc.
    private String resourceType;      // "video" or "image"
    private Long sizeInBytes;         // File size
    private Integer width;            // Image/video width
    private Integer height;           // Image/video height

    // Social media like info
    private String description;       // Caption or description
    private Long likes = 0L;          // Number of likes
    private Long views = 0L;          // Number of views
    private Long comments = 0L;       // Number of comments
    private String hashtags;           // Optional hashtags, comma separated
    private String category;           // Optional category or type

    // Optional user info
    private String uploadedBy;         // Username or userId who uploaded
    private Boolean isPublic = true;   // Whether reel is public or private

    // Timestamps
    private Long createdAt = System.currentTimeMillis();
    private Long updatedAt;            // Optional last updated timestamp
}
