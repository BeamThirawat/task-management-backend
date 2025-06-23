package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.request.FolderRequestDto;
import com.example.taskmanagement.dto.response.FolderResponseDto;
import com.example.taskmanagement.entity.Folders;
import com.example.taskmanagement.repository.FoldersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class FoldersService {

    @Autowired
    private FoldersRepository repository;

    // get folders by id
    public FolderResponseDto getFolder(Long id) {
        return repository.findById(id).map(folders -> {
            FolderResponseDto folderResponseDto = new FolderResponseDto();
            folderResponseDto.setFolderName(folders.getFolderName());

            return folderResponseDto;
        }).orElseThrow(()-> new RuntimeException("Folder not found ID: "+id));
    }

    // get List folders by user_id
    public List<Folders> getFolders(Long user_id) {
        return repository.findByUserId(user_id);
    }

    // add folder
    public Folders addFolder(FolderRequestDto requestDto) {

        Folders folders = new Folders();
        folders.setFolderName(requestDto.getFolderName())
        .setUserId(requestDto.getUserId())
        .setCreatedAt(LocalDateTime.now());

        return repository.save(folders);
    }

    // edit folder
    public Folders editFolder(FolderRequestDto requestDto, Long id) {
        return repository.findById(id)
                .map(folders -> {
                    folders.setFolderName(requestDto.getFolderName())
                            .setUserId(requestDto.getUserId())
                            .setUpdatedAt(LocalDateTime.now());
                    return repository.save(folders);
                }).orElseThrow(()-> new RuntimeException("Folder not found ID: "+id));
    }

    // delete folder
    public void deleteFolder(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Folder not found with ID: " + id);
        }

        repository.deleteById(id);
    }

}
