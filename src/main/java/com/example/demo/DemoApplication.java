package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;


@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CloudinaryConfig cloudinaryConfig;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("✅ Spring Boot Backend setup success! 🚀");

        // Print MongoDB database
        String dbName = mongoTemplate.getDb().getName();
        System.out.println("Connected to MongoDB database: " + dbName);

        // Print Cloudinary credentials
        System.out.println("Cloudinary Cloud Name: " + cloudinaryConfig.getCloudName());
        System.out.println("Cloudinary API Key: " + cloudinaryConfig.getApiKey());
        System.out.println("Cloudinary API Secret: " + cloudinaryConfig.getApiSecret());
    }
}
