package com.example.taskmanagement.service;

import com.example.taskmanagement.controller.TaskController;
import com.example.taskmanagement.dto.request.TaskRequestDto;
import com.example.taskmanagement.dto.response.TaskResponseDto;
import com.example.taskmanagement.entity.Folders;
import com.example.taskmanagement.entity.Tasks;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.repository.FoldersRepository;
import com.example.taskmanagement.repository.TasksRepository;
import com.example.taskmanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class TaskService {

    @Autowired
    private TasksRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoldersRepository foldersRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    // get list task by email
    public List<TaskResponseDto.GetTaskResponseDto> getTasks(String email) {
        logger.info("Fetching task list for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new RuntimeException("User not found");
                });

        List<Tasks> tasksList = repository.findByUserIdOrderByIdDesc(user.getId());
        logger.debug("Found {} tasks for user ID: {}", tasksList.size(), user.getId());

        return tasksList.stream()
                .map(tasks -> new TaskResponseDto.GetTaskResponseDto()
                        .setId(tasks.getId())
                        .setTitle(tasks.getTitle())
                        .setDescription(tasks.getDescription())
                        .setStatus(tasks.getStatus())
                        .setUserId(tasks.getUser().getId())
                        .setFolderId(tasks.getFolder().getId())
                        .setStartDate(tasks.getStartDate()))
                .collect(Collectors.toList());
    }

    // get list task by status
    public List<TaskResponseDto.GetTaskResponseDto> getTasksByStatus(Tasks.TaskStatus status, String email) {
        logger.info("Fetching tasks by status [{}] for user: {}", status, email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new RuntimeException("User not found");
                });

        List<Tasks> tasksList = repository.findByStatusAndUserIdOrderByIdDesc(status, user.getId());
        logger.debug("Found {} tasks with status [{}] for user ID: {}", tasksList.size(), status, user.getId());

        return tasksList.stream()
                .map(task -> new TaskResponseDto.GetTaskResponseDto()
                        .setId(task.getId())
                        .setTitle(task.getTitle())
                        .setDescription(task.getDescription())
                        .setStatus(task.getStatus())
                        .setUserId(task.getUser().getId())
                        .setFolderId(task.getFolder().getId())
                        .setStartDate(task.getStartDate()))
                .collect(Collectors.toList());
    }

    // get task detail by id
    public TaskResponseDto.GetTaskResponseDto getTask(Long id, String email) {
        logger.info("Fetching task with ID: {} for user: {}", id, email);
        Tasks task = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Task not found with ID: {}", id);
                    return new RuntimeException("Task not found with ID: " + id);
                });

        if (!task.getUser().getEmail().equals(email)) {
            logger.warn("Unauthorized access attempt to task ID: {} by user: {}", id, email);
            throw new RuntimeException("You are not allowed to get this task");
        }

        return new TaskResponseDto.GetTaskResponseDto()
                .setId(task.getId())
                .setTitle(task.getTitle())
                .setDescription(task.getDescription())
                .setStatus(task.getStatus())
                .setUserId(task.getUser().getId())
                .setFolderId(task.getFolder().getId())
                .setStartDate(task.getStartDate());
    }

    // add task
    public TaskResponseDto.GetTaskResponseDto addTask(TaskRequestDto.AddAndUpdateTaskRequestDto requestDto, String email) {
        logger.info("Adding new task for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new RuntimeException("User not found");
                });

        Folders folder = foldersRepository.findById(requestDto.getFolderId())
                .orElseThrow(() -> {
                    logger.warn("Folder not found with ID: {}", requestDto.getFolderId());
                    return new RuntimeException("Folder not found");
                });

        if (!folder.getUser().getId().equals(user.getId())) {
            logger.warn("Unauthorized folder access: folder ID {} does not belong to user {}", folder.getId(), email);
            throw new RuntimeException("You are not allowed to use this folder");
        }

        Tasks tasks = new Tasks()
                .setTitle(requestDto.getTitle())
                .setDescription(requestDto.getDescription())
                .setStatus(requestDto.getStatus())
                .setUser(user)
                .setFolder(folder)
                .setStartDate(requestDto.getStartDate())
                .setCreatedAt(LocalDateTime.now());

        Tasks saveTask = repository.save(tasks);
        logger.info("Task added successfully with ID: {}", saveTask.getId());

        return new TaskResponseDto.GetTaskResponseDto()
                .setId(saveTask.getId())
                .setTitle(saveTask.getTitle())
                .setDescription(saveTask.getDescription())
                .setStatus(saveTask.getStatus())
                .setUserId(saveTask.getUser().getId())
                .setFolderId(saveTask.getFolder().getId())
                .setStartDate(saveTask.getStartDate());
    }

    // edit task
    public TaskResponseDto.GetTaskResponseDto editTask(TaskRequestDto.AddAndUpdateTaskRequestDto requestDto, Long id, String email) {
        logger.info("Editing task ID: {} by user: {}", id, email);
        Tasks task = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Task not found with ID: {}", id);
                    return new RuntimeException("Task not found ID: " + id);
                });

        if (!task.getUser().getEmail().equals(email)) {
            logger.warn("Unauthorized edit attempt on task ID: {} by user: {}", id, email);
            throw new RuntimeException("You are not allowed to edit this task");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new RuntimeException("User not found");
                });

        Folders folder = foldersRepository.findById(requestDto.getFolderId())
                .orElseThrow(() -> {
                    logger.warn("Folder not found with ID: {}", requestDto.getFolderId());
                    return new RuntimeException("Folder not found");
                });

        if (!folder.getUser().getId().equals(user.getId())) {
            logger.warn("Unauthorized folder access while editing: folder ID {} does not belong to user {}", folder.getId(), email);
            throw new RuntimeException("You are not allowed to use this folder");
        }

        task.setTitle(requestDto.getTitle())
                .setDescription(requestDto.getDescription())
                .setStatus(requestDto.getStatus())
                .setUser(user)
                .setStartDate(requestDto.getStartDate())
                .setFolder(folder)
                .setUpdatedAt(LocalDateTime.now());

        Tasks saveTask = repository.save(task);
        logger.info("Task updated successfully with ID: {}", saveTask.getId());

        return new TaskResponseDto.GetTaskResponseDto()
                .setId(saveTask.getId())
                .setTitle(saveTask.getTitle())
                .setDescription(saveTask.getDescription())
                .setStatus(saveTask.getStatus())
                .setUserId(saveTask.getUser().getId())
                .setFolderId(saveTask.getFolder().getId())
                .setStartDate(saveTask.getStartDate());
    }

    // delete task
    public void deleteTask(Long id, String email) {
        logger.info("Deleting task with ID: {} by user: {}", id, email);
        Tasks task = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Task not found with ID: {}", id);
                    return new RuntimeException("Task not found with ID: " + id);
                });

        if (!task.getUser().getEmail().equals(email)) {
            logger.warn("Unauthorized delete attempt on task ID: {} by user: {}", id, email);
            throw new RuntimeException("You are not allowed to delete this task");
        }

        repository.deleteById(id);
        logger.info("Task deleted successfully with ID: {}", id);
    }
}
