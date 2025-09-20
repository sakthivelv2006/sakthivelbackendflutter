package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "uploaded_files")
public class FileModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String assetId;
    private String publicId;
    private String originalFilename;
    private String format;
    private String resourceType;
    private String url;
    private String secureUrl;
    private Long bytes;
    private Integer width;
    private Integer height;
    private String signature;
}
