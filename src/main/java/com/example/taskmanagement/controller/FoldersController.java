package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.request.FolderRequestDto;
import com.example.taskmanagement.dto.response.FolderResponseDto;
import com.example.taskmanagement.dto.response.StandardResponseDto;
import com.example.taskmanagement.entity.Folders;
import com.example.taskmanagement.service.FoldersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/folder-management")
@Tag(name = "Folder Management", description = "API For Folder Management")
public class FoldersController {

    @Autowired
    private FoldersService service;

    @Operation(summary = "Get List Folder By Email")
    @GetMapping(value = "getFolders")
    public StandardResponseDto<List<FolderResponseDto>> getFolders() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return StandardResponseDto.createSuccessResponse(service.getFolders(email));
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }

    @Operation(summary = "Get Folder by ID")
    @GetMapping(value = "getFolder")
    public StandardResponseDto<FolderResponseDto> getFolder(@RequestParam Long id) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return StandardResponseDto.createSuccessResponse(service.getFolder(id, email));
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }

    @Operation(summary = "Add New Folder")
    @PostMapping(value = "addFolder")
    public StandardResponseDto<FolderResponseDto> AddFolder(@RequestBody FolderRequestDto requestDto) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return StandardResponseDto.createSuccessResponse(service.addFolder(requestDto, email));
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }

    @Operation(summary = "Edit Folder")
    @PostMapping(value = "editFolder")
    public StandardResponseDto<FolderResponseDto> EditFolder(@RequestBody FolderRequestDto requestDto, @RequestParam Long id){
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return StandardResponseDto.createSuccessResponse(service.editFolder(requestDto, id, email));
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }

    @Operation(summary = "Delete Folder")
    @DeleteMapping(value = "deleteFolder")
    public StandardResponseDto<String> deleteFolder(@RequestParam Long id) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            service.deleteFolder(id, email);
            return StandardResponseDto.createSuccessResponse("Delete Folder Success");
        } catch (Exception e) {
           return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }
}
