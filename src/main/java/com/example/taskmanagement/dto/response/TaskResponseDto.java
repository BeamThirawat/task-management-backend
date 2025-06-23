package com.example.taskmanagement.dto.response;

import com.example.taskmanagement.entity.Tasks;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

public class TaskResponseDto {

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetTaskResponseDto {
        private Long id;
        private String title;
        private String description;
        private Tasks.TaskStatus status;

        @JsonProperty("user_id")
        private Long userId;

        @JsonProperty("folder_id")
        private Long folderId;
        private LocalDate startDate;
    }
}
