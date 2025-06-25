package com.example.taskmanagement.dto.request;

import com.example.taskmanagement.entity.Tasks;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

public class TaskRequestDto {

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetTaskRequestDto {
        private Long id;
        private String title;
        private String description;
        private Tasks.TaskStatus status;

        @JsonProperty("folder_id")
        private Long folderId;
        @JsonProperty("start_date")
        private LocalDate startDate;
    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddAndUpdateTaskRequestDto {
        private String title;
        private String description;
        private Tasks.TaskStatus status;

        @JsonProperty("folder_id")
        private Long folderId;
        @JsonProperty("start_date")
        private LocalDate startDate;
    }

}
