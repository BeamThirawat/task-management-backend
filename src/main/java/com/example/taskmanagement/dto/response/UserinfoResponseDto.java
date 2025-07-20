package com.example.taskmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserinfoResponseDto {
    private Long id;
    private String username;
    private String email;
}
