package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.request.FolderRequestDto;
import com.example.taskmanagement.dto.response.FolderResponseDto;
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
public class FoldersService {

    @Autowired
    private FoldersRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TasksRepository tasksRepository;

    private static final Logger logger = LoggerFactory.getLogger(FoldersService.class);

    // get folders by email
    public FolderResponseDto getFolder(Long id, String email) {
        logger.info("Fetching folder by ID: {} and email: {}", id, email);

        Folders folder = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Folder not found with ID: {}", id);
                    return new RuntimeException("Folder not found ID: " + id);
                });

        if (!folder.getUser().getEmail().equals(email)) {
            logger.warn("Access denied: email {} tried to access folder ID {} not owned by them", email, id);
            throw new RuntimeException("You are not allowed to get this folder");
        }

        logger.debug("Creating FolderResponseDto for folder ID: {}", id);
        return new FolderResponseDto()
                .setId(folder.getId())
                .setFolderName(folder.getFolderName());
    }

    // get List folders by email
    public List<FolderResponseDto> getFolders(String email) {
        logger.info("Fetching folder list for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new RuntimeException("User not found");
                });

        List<Folders> foldersList = repository.findByUserIdOrderByIdDesc(user.getId());

        logger.debug("Found {} folders for user ID: {}", foldersList.size(), user.getId());

        return foldersList.stream()
                .map(folder -> new FolderResponseDto()
                        .setId(folder.getId())
                        .setFolderName(folder.getFolderName()))
                .collect(Collectors.toList());
    }

    // add folder
    public FolderResponseDto addFolder(FolderRequestDto requestDto, String email) {
        logger.info("Adding new folder '{}' for email: {}", requestDto.getFolderName(), email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new RuntimeException("User not found");
                });

        Folders folders = new Folders()
                .setFolderName(requestDto.getFolderName())
                .setUser(user)
                .setCreatedAt(LocalDateTime.now());

        Folders savedFolder = repository.save(folders);

        logger.info("Folder saved with ID: {}", savedFolder.getId());

        return new FolderResponseDto()
                .setId(savedFolder.getId())
                .setFolderName(savedFolder.getFolderName());
    }

    // edit folder
    public FolderResponseDto editFolder(FolderRequestDto requestDto, Long id, String email) {
        logger.info("Editing folder ID: {} for email: {}", id, email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new RuntimeException("User not found");
                });

        Folders folder = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Folder not found with ID: {}", id);
                    return new RuntimeException("Folder not found");
                });

        if (!folder.getUser().getEmail().equals(email)) {
            logger.warn("Unauthorized edit attempt by {} on folder ID: {}", email, id);
            throw new RuntimeException("You are not allowed to edit this folder");
        }

        folder.setFolderName(requestDto.getFolderName())
                .setUser(user)
                .setUpdatedAt(LocalDateTime.now());

        Folders savedFolder = repository.save(folder);

        logger.info("Folder updated with ID: {}", savedFolder.getId());

        return new FolderResponseDto()
                .setId(savedFolder.getId())
                .setFolderName(savedFolder.getFolderName());
    }

    // delete folder
    public void deleteFolder(Long id, String email) {
        logger.info("Deleting folder ID: {} for email: {}", id, email);

        Folders folder = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Folder not found with ID: {}", id);
                    return new RuntimeException("Folder not found with ID: " + id);
                });

        if (!folder.getUser().getEmail().equals(email)) {
            logger.warn("Unauthorized delete attempt by {} on folder ID: {}", email, id);
            throw new RuntimeException("You are not allowed to delete this task");
        }

        logger.debug("Deleting tasks in folder ID: {}", id);
        tasksRepository.deleteByFolderId(id);

        logger.debug("Deleting folder ID: {}", id);
        repository.deleteById(id);

        logger.info("Folder and associated tasks deleted successfully for ID: {}", id);
    }

}
