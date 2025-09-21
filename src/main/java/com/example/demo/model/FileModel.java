package com.example.demo.model;

import com.example.demo.enumclassess.Filetype;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "uploaded_files") // MongoDB collection
public class FileModel {

    @Id
    private String id; // MongoDB ID

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

    private String userId; // store user reference as String (MongoDB style)
    
    private String description; 
    
    private Filetype type;

    private String comments;
    private Integer likes;

}
