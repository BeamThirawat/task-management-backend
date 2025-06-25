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

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    // get folders by email
    public FolderResponseDto getFolder(Long id, String email) {
        logger.info("##### Get folders by email #####");
        Folders folder = repository.findById(id).orElseThrow(() -> new RuntimeException("Folder not found ID: " + id));

        // validate email before get folder
        logger.info("##### validate email before get folder #####");
        if (!folder.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to get this folder");
        }

        logger.info("##### Create folderResponseDto #####");
        FolderResponseDto folderResponseDto = new FolderResponseDto();
        folderResponseDto.
                setId(folder.getId())
                .setFolderName(folder.getFolderName());

        return folderResponseDto;
    }

    // get List folders by email
    public List<FolderResponseDto> getFolders(String email) {
        logger.info("##### Get list folders by email #####");
        // get user by email (Auth => cookie)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Folders> foldersList = repository.findByUserIdOrderByIdDesc(user.getId());
        List<FolderResponseDto> folderResponseDtoList = new ArrayList<>();

        // map Folders -> FolderResponseDto
        return foldersList.stream()
                .map(folder -> new FolderResponseDto()
                        .setId(folder.getId())
                        .setFolderName(folder.getFolderName()))
                .collect(Collectors.toList());
    }

    // add folder
    public FolderResponseDto addFolder(FolderRequestDto requestDto, String email) {
        logger.info("##### Add folder #####");
        // get user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Folders folders = new Folders();
        folders.setFolderName(requestDto.getFolderName())
                .setUser(user)
                .setCreatedAt(LocalDateTime.now());

        Folders savedFolder = repository.save(folders);

        return new FolderResponseDto()
                .setId(savedFolder.getId())
                .setFolderName(savedFolder.getFolderName());
    }

    // edit folder
    public FolderResponseDto editFolder(FolderRequestDto requestDto, Long id, String email) {
        logger.info("##### Edit folder #####");
        // get user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // get folder by id
        Folders folder = repository.findById(id).orElseThrow(() -> new RuntimeException("Folder not found"));

        // validate email before edit folder
        if (!folder.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to edit this folder");
        }

        folder.setFolderName(requestDto.getFolderName())
                .setUser(user)
                .setUpdatedAt(LocalDateTime.now());

        Folders savedFolder = repository.save(folder);

        return new FolderResponseDto()
                .setId(savedFolder.getId())
                .setFolderName(savedFolder.getFolderName());
    }

    // delete folder
    public void deleteFolder(Long id, String email) {
        logger.info("##### Delete folder #####");
        // get task by id
        Folders folder = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Folder not found with ID: " + id));

        // validate email before delete
        if (!folder.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to delete this task");
        }

        // task in folder
        logger.info("##### Delete task #####");
        tasksRepository.deleteByFolderId(id);
        // delete folder
        logger.info("##### Delete folder #####");
        repository.deleteById(id);
    }

}
