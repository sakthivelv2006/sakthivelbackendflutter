package com.example.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String role;

    public User() {
        this.role = Role.USER.name(); // default value
    }

    public enum Role {
        USER,
        ADMIN,
        AGENT
    }
}
