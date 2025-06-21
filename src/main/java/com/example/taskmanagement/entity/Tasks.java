package com.example.taskmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Accessors(chain = true)
@Table(name = "Tasks")
public class Tasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "folder_id")
    private Long folder_id;

    @Column(name = "start_date")
    private LocalDate start_date;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    public enum TaskStatus {
        TO_DO,
        IN_PROGRESS,
        DONE
    }
}
