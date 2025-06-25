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
        // get user by email (Auth => cookie)
        logger.info("##### Get list tasks by email #####");
        logger.info("##### get user by email (Auth => cookie) #####");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        logger.info("##### Get tasks list and create taskResponseDtoList #####");
        List<Tasks> tasksList = repository.findByUserIdOrderByIdDesc(user.getId());
        List<TaskResponseDto.GetTaskResponseDto> taskResponseDtoList = new ArrayList<>();

        // map Tasks -> TaskResponseDto.GetTaskResponseDto
        logger.info("##### Map tasks #####");
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
        // get user
        logger.info("##### Get task by status #####");
        logger.info("##### get user by email (Auth => cookie) #####");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // query task for status
        logger.info("##### Get Tasks by status #####");
        List<Tasks> tasksList = repository.findByStatusAndUserIdOrderByIdDesc(status, user.getId());

        // map to DTO
        logger.info("##### Map tasks #####");
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
        // get task by id
        logger.info("##### Get task by id #####");
        logger.info("##### get user by email (Auth => cookie) #####");
        Tasks task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));

        // validate email before get set TaskResponseDto
        logger.info("##### Validate email before get set TaskResponseDto #####");
        if (!task.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to get this task");
        }

            TaskResponseDto.GetTaskResponseDto taskResponseDto = new TaskResponseDto.GetTaskResponseDto();
            taskResponseDto.setId(task.getId())
                    .setTitle(task.getTitle())
                    .setDescription(task.getDescription())
                    .setStatus(task.getStatus())
                    .setUserId(task.getUser().getId())
                    .setFolderId(task.getFolder().getId())
                    .setStartDate(task.getStartDate());
            return taskResponseDto;
    }

    // add task
    public TaskResponseDto.GetTaskResponseDto addTask(TaskRequestDto.AddAndUpdateTaskRequestDto requestDto, String email) {
        logger.info("##### Add task #####");
        logger.info("##### get user by email (Auth => cookie) #####");
        // get user by email (Auth => cookie)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // get folder by folderId
        logger.info("##### get folder by folderId #####");
        Folders folder = foldersRepository.findById(requestDto.getFolderId())
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        // validate folder
        logger.info("##### Validate folder #####");
        if (!folder.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to use this folder");
        }

        // Add Task
        logger.info("Add task");
        Tasks tasks = new Tasks();
        tasks.setTitle(requestDto.getTitle())
                .setDescription(requestDto.getDescription())
                .setStatus(requestDto.getStatus())
                .setUser(user)
                .setFolder(folder)
                .setStartDate(requestDto.getStartDate())
                .setCreatedAt(LocalDateTime.now());
        Tasks saveTask = repository.save(tasks);

        logger.info("Return taskResponseDto");
        return new TaskResponseDto.GetTaskResponseDto()
                .setId(saveTask.getId())
                .setTitle(saveTask.getTitle())
                .setDescription(saveTask.getDescription())
                .setStatus(saveTask.getStatus())
                .setUserId(saveTask.getUser().getId())
                .setFolderId(saveTask.getFolder().getId())
                .setStartDate(saveTask.getStartDate());
    }

     //edit task
    public TaskResponseDto.GetTaskResponseDto editTask(TaskRequestDto.AddAndUpdateTaskRequestDto requestDto, Long id, String email) {
        logger.info("##### Get task from DB");
        // get task from DB
        Tasks task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found ID: " + id));

        logger.info("##### validate email before edit #####");
        // validate email before edit
        if (!task.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to edit this task");
        }

        // get user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // get folder by folderId
        Folders folder = foldersRepository.findById(requestDto.getFolderId())
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        logger.info("##### validate folder #####");
        // validate folder
        if (!folder.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to use this folder");
        }

        logger.info("##### save #####");
        task.setTitle(requestDto.getTitle())
                .setDescription(requestDto.getDescription())
                .setStatus(requestDto.getStatus())
                .setUser(user)
                .setStartDate(requestDto.getStartDate())
                .setFolder(folder)
                .setUpdatedAt(LocalDateTime.now());

        Tasks saveTask = repository.save(task);

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
        logger.info("##### Delete Task #####");
        // get task by id
        Tasks task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));

        // validate email before delete
        if (!task.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to delete this task");
        }

        repository.deleteById(id);
    }
}
