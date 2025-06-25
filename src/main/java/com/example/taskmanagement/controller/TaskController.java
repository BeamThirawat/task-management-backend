package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.request.TaskRequestDto;
import com.example.taskmanagement.dto.response.StandardResponseDto;
import com.example.taskmanagement.dto.response.TaskResponseDto;
import com.example.taskmanagement.entity.Tasks;
import com.example.taskmanagement.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/task-management")
@Tag(name = "Task management", description = "API For Task management")
public class TaskController {

    @Autowired
    private TaskService service;

    @Operation(description = "Get List Task By User ID")
    @GetMapping(value = "getTasks")
    public StandardResponseDto<List<TaskResponseDto.GetTaskResponseDto>> getTasks(@RequestParam(required = false) Tasks.TaskStatus status) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            if (status != null) {
                return StandardResponseDto.createSuccessResponse(service.getTasksByStatus(status, email));
            }
            return StandardResponseDto.createSuccessResponse(service.getTasks(email));
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }

    @Operation(description = "Get Task detail by id")
    @GetMapping(value = "getTask")
    public StandardResponseDto<TaskResponseDto.GetTaskResponseDto> getTask(@RequestParam Long id) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return StandardResponseDto.createSuccessResponse(service.getTask(id, email));
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }

    @Operation(description = "Add Task")
    @PostMapping(value = "addTask")
    public StandardResponseDto<TaskResponseDto.GetTaskResponseDto> addTask(@RequestBody TaskRequestDto.AddAndUpdateTaskRequestDto requestDto) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return StandardResponseDto.createSuccessResponse(service.addTask(requestDto, email));
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }

    @Operation(description = "Edit Task")
    @PostMapping(value = "editTask")
    public StandardResponseDto<TaskResponseDto.GetTaskResponseDto> editTask(@RequestBody TaskRequestDto.AddAndUpdateTaskRequestDto requestDto,
                                               @RequestParam Long id) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return StandardResponseDto.createSuccessResponse(service.editTask(requestDto, id, email));
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }

    @Operation(description = "Delete Task")
    @DeleteMapping(value = "deleteTask")
    public StandardResponseDto<String> deleteTask(@RequestParam Long id) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            service.deleteTask(id,email);
            return StandardResponseDto.createSuccessResponse("delete Task Success");
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }


}
