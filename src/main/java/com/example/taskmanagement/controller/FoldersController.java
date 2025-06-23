package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.request.FolderRequestDto;
import com.example.taskmanagement.dto.response.FolderResponseDto;
import com.example.taskmanagement.dto.response.StandardResponseDto;
import com.example.taskmanagement.entity.Folders;
import com.example.taskmanagement.service.FoldersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/folder-management")
@Tag(name = "Folder Management", description = "API For Folder Management")
public class FoldersController {

    @Autowired
    private FoldersService service;

    @Operation(summary = "Get List Folder By User ID")
    @GetMapping(value = "getFolders/{user_id}")
    public StandardResponseDto<List<Folders>> getFolders(@PathVariable Long user_id) {
        try {
            return StandardResponseDto.createSuccessResponse(service.getFolders(user_id));
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }

    @Operation(summary = "Get Folder by ID")
    @GetMapping(value = "getFolder/{id}")
    public StandardResponseDto<FolderResponseDto> getFolder(@PathVariable Long id) {
        try {
            return StandardResponseDto.createSuccessResponse(service.getFolder(id));
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }

    @Operation(summary = "Add New Folder")
    @PostMapping(value = "addFolder")
    public StandardResponseDto<Folders> AddFolder(@RequestBody FolderRequestDto requestDto) {
        try {
            return StandardResponseDto.createSuccessResponse(service.addFolder(requestDto));
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }

    @Operation(summary = "Edit Folder")
    @PostMapping(value = "editFolder/{id}")
    public StandardResponseDto<Folders> EditFolder(@RequestBody FolderRequestDto requestDto, @PathVariable Long id){
        try {
            return StandardResponseDto.createSuccessResponse(service.editFolder(requestDto, id));
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }

    @Operation(summary = "Delete Folder")
    @DeleteMapping(value = "deleteFolder/{id}")
    public StandardResponseDto<String> deleteFolder(@PathVariable Long id) {
        try {
            service.deleteFolder(id);
            return StandardResponseDto.createSuccessResponse("Delete Folder Success");
        } catch (Exception e) {
           return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }
}
