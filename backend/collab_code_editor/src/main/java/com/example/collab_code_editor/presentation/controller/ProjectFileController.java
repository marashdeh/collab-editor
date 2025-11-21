package com.example.collab_code_editor.presentation.controller;

import com.example.collab_code_editor.core.dto.FileVersionDto;
import com.example.collab_code_editor.core.dto.ProjectFileDto;
import com.example.collab_code_editor.core.model.CollaboratorRole;
import com.example.collab_code_editor.core.security.JwtTokenService;
import com.example.collab_code_editor.core.service.ProjectFileService;
import com.example.collab_code_editor.core.service.CollaboratorService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class ProjectFileController {

    private final ProjectFileService fileService;
    private final CollaboratorService collaboratorService;
    private final JwtTokenService jwt;

    private Long getUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer "))
            throw new RuntimeException("Missing or invalid Authorization header");
        return Long.valueOf(jwt.getUserId(auth.substring(7)));
    }

    private void checkOwner(Long userId, Long projectId) {
        CollaboratorRole role = collaboratorService.getRoleForUserAndProject(userId, projectId);
        if (role != CollaboratorRole.OWNER)
            throw new RuntimeException("Only OWNER can perform this action");
    }

    @PostMapping("/{folderId}")
    public ResponseEntity<ProjectFileDto> createFile(
            @PathVariable Long folderId,
            @RequestBody ProjectFileDto dto,
            HttpServletRequest request) {

        Long userId = getUserId(request);
        checkOwner(userId, dto.getProjectId());
        return ResponseEntity.ok(fileService.createFile(dto, folderId));
    }

    @PutMapping("/{fileId}")
    public ResponseEntity<ProjectFileDto> renameFile(
            @PathVariable Long fileId,
            @RequestParam String newName,
            HttpServletRequest request) {

        Long userId = getUserId(request);
        ProjectFileDto file = fileService.getFileById(fileId);
        checkOwner(userId, file.getProjectId());

        return ResponseEntity.ok(fileService.renameFile(fileId, newName));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(
            @PathVariable Long fileId,
            HttpServletRequest request) {

        Long userId = getUserId(request);
        ProjectFileDto file = fileService.getFileById(fileId);
        checkOwner(userId, file.getProjectId());

        fileService.deleteFile(fileId);
        return ResponseEntity.ok("File deleted");
    }

    @PutMapping("/{fileId}/restore")
    public ResponseEntity<String> restoreFile(
            @PathVariable Long fileId,
            HttpServletRequest request) {

        Long userId = getUserId(request);
        ProjectFileDto file = fileService.getFileById(fileId);
        checkOwner(userId, file.getProjectId());

        fileService.restoreFile(fileId);
        return ResponseEntity.ok("File restored");
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<ProjectFileDto>> listFilesByFolder(@PathVariable Long folderId) {
        return ResponseEntity.ok(fileService.listFilesByFolder(folderId));
    }

    @GetMapping("/{fileId}/versions")
    public ResponseEntity<List<FileVersionDto>> listFileVersions(@PathVariable Long fileId) {
        return ResponseEntity.ok(fileService.listVersions(fileId));
    }

    @PutMapping("/{fileId}/update")
    public ResponseEntity<ProjectFileDto> updateFileContent(
            @PathVariable Long fileId,
            @RequestBody Map<String, String> body) {

        return ResponseEntity.ok(fileService.updateFileContent(fileId, body.get("content")));
    }
}
