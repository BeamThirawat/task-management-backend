package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.request.TaskRequestDto;
import com.example.taskmanagement.dto.response.TaskResponseDto;
import com.example.taskmanagement.entity.Tasks;
import com.example.taskmanagement.repository.TasksRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class TaskService {

    @Autowired
    private TasksRepository repository;

    // get list task by user_id
    public List<Tasks> getTasks(Long user_id) {
        return repository.findByUserId(user_id);
    }

    // get list task by status
    public List<Tasks> getTasksByStatus(Tasks.TaskStatus status) {
        return repository.findByStatus(status);
    }

    // get task detail by id
    public TaskResponseDto.GetTaskResponseDto getTask(Long id) {
        return repository.findById(id).map(tasks -> {
            TaskResponseDto.GetTaskResponseDto taskResponseDto = new TaskResponseDto.GetTaskResponseDto();
            taskResponseDto.setId(tasks.getId())
                    .setTitle(tasks.getTitle())
                    .setDescription(tasks.getDescription())
                    .setStatus(tasks.getStatus())
                    .setUserId(tasks.getUserId())
                    .setFolderId(tasks.getFolderId())
                    .setStartDate(tasks.getStartDate());
            return taskResponseDto;
        }).orElseThrow(()-> new RuntimeException("Task not found ID: "+id));
    }

    // add task
    public Tasks addTask(TaskRequestDto.AddAndUpdateTaskRequestDto requestDto) {
        Tasks tasks = new Tasks();
        tasks.setTitle(requestDto.getTitle())
                .setDescription(requestDto.getDescription())
                .setStatus(requestDto.getStatus())
                .setUserId(requestDto.getUserId())
                .setFolderId(requestDto.getFolderId())
                .setStartDate(requestDto.getStartDate())
                .setCreatedAt(LocalDateTime.now());
        return repository.save(tasks);
    }

     //edit task
    public Tasks editTask(TaskRequestDto.AddAndUpdateTaskRequestDto requestDto, Long id) {
        return repository.findById(id).map(tasks -> {
            tasks.setTitle(requestDto.getTitle())
                    .setDescription(requestDto.getDescription())
                    .setStatus(requestDto.getStatus())
                    .setUserId(requestDto.getUserId())
                    .setStartDate(requestDto.getStartDate())
                    .setFolderId(requestDto.getFolderId())
                    .setUpdatedAt(LocalDateTime.now());
            return repository.save(tasks);
        }).orElseThrow(()-> new RuntimeException("Task not found ID: "+id));
    }

    // delete task
    public void deleteTask(Long id, String email) {
        Tasks task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));

//        if (!task.getUser().getEmail().equals(email)) {
//            throw new RuntimeException("You are not allowed to delete this task");
//        }

        repository.deleteById(id);
    }
}
