package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;  // Inject MongoTemplate

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Print success message
        System.out.println("✅ Spring Boot Backend setup success! 🚀");

        // Print the connected MongoDB database name
        String dbName = mongoTemplate.getDb().getName();
        System.out.println("Connected to MongoDB database: " + dbName);
    }
}
