package com.example.taskmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Accessors(chain = true)
@Table(name = "folders")
public class Folders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "folder_name")
    private String folder_name;

    @Column(name = "user_id")
    private Long user_id;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;
}
