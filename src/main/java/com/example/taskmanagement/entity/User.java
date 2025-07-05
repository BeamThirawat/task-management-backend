package com.example.taskmanagement.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Accessors(chain = true)
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "google_id")
    @JsonProperty("google_id")
    private String googleId;

    @CreationTimestamp
    @Column(name = "created_at")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    // Constructor for Google OAuth
    public User(String email, String username, String googleId) {
        this.email = email;
        this.username = username;
        this.googleId = googleId;
        this.createdAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean isGoogleUser() {
        return googleId != null && !googleId.isEmpty();
    }
}
