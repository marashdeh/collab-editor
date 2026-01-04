package com.example.collab_code_editor.presentation.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import com.example.collab_code_editor.core.dto.FileVersionDto;
import com.example.collab_code_editor.core.dto.ProjectFileDto;
import com.example.collab_code_editor.core.model.CollaboratorRole;
import com.example.collab_code_editor.core.model.Project; // ✅ Added
import com.example.collab_code_editor.core.security.JwtTokenService;
import com.example.collab_code_editor.core.service.ProjectFileService;
import com.example.collab_code_editor.core.service.CollaboratorService;
import com.example.collab_code_editor.infrastructure.repository.ProjectRepository; // ✅ Added

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
    private final ProjectRepository projectRepository; // ✅ Injection for permission fix
    private final JwtTokenService jwt;

    private Long getUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer "))
            throw new RuntimeException("Missing or invalid Authorization header");
        return jwt.extractUserId(auth.substring(7));
    }

    // ✅ FIXED: Checks Direct Ownership first (Fixes Legacy Project 403 Error)
    private void checkOwner(Long userId, Long projectId) {
        // 1. Check Project Table (The ultimate truth)
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (project.getOwner().getId().equals(userId)) {
            return; // Access Granted: You are the Owner
        }

        // 2. Check Collaborators Table
        CollaboratorRole role = collaboratorService.getRoleForUserAndProject(userId, projectId);
        if (role != CollaboratorRole.OWNER) {
            throw new RuntimeException("User is not the owner of this project");
        }
    }

    // ✅ FIXED: Checks Direct Ownership OR Collaborator status
    private void checkEditor(Long userId, Long projectId) {
        // 1. Check Project Table
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (project.getOwner().getId().equals(userId)) {
            return; // Access Granted: You are the Owner
        }

        // 2. Check Collaborators Table
        CollaboratorRole role = collaboratorService.getRoleForUserAndProject(userId, projectId);
        if (role == null) {
            throw new RuntimeException("User is not a member of this project");
        }
    }

    @PostMapping
    public ResponseEntity<ProjectFileDto> createFile(
            @RequestBody ProjectFileDto dto,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        checkEditor(userId, dto.getProjectId()); // Editors can usually create files
        return ResponseEntity.ok(fileService.createFile(dto, dto.getFolderId()));
    }

    @PutMapping("/{fileId}")
    public ResponseEntity<ProjectFileDto> renameFile(
            @PathVariable Long fileId,
            @RequestParam String newName,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        ProjectFileDto file = fileService.getFileById(fileId);
        checkEditor(userId, file.getProjectId()); // Editors can usually rename
        return ResponseEntity.ok(fileService.renameFile(fileId, newName));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(
            @PathVariable Long fileId,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        ProjectFileDto file = fileService.getFileById(fileId);
        checkEditor(userId, file.getProjectId()); // Editors can delete
        fileService.deleteFile(fileId);
        return ResponseEntity.ok("File deleted");
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<ProjectFileDto>> listFilesByFolder(@PathVariable Long folderId) {
        return ResponseEntity.ok(fileService.listFilesByFolder(folderId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ProjectFileDto>> listFilesByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(fileService.getFilesByProject(projectId));
    }

    @GetMapping("/{fileId}/versions")
    public ResponseEntity<List<FileVersionDto>> listFileVersions(@PathVariable Long fileId) {
        return ResponseEntity.ok(fileService.listVersions(fileId));
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<ProjectFileDto> getFileContent(@PathVariable Long fileId) {
        return ResponseEntity.ok(fileService.getFileById(fileId));
    }

    @PutMapping("/{fileId}/update")
    public ResponseEntity<ProjectFileDto> updateFileContent(
            @PathVariable Long fileId,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        ProjectFileDto file = fileService.getFileById(fileId);
        checkEditor(userId, file.getProjectId());
        return ResponseEntity.ok(fileService.updateFileContent(fileId, body.get("content")));
    }

    @PostMapping("/{fileId}/cursor")
    public ResponseEntity<String> saveCursor(
            @PathVariable Long fileId,
            @RequestParam int line,
            @RequestParam int col,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        // Cursors don't strictly need a DB check every ms, but good practice if not too slow
        fileService.saveCursorPosition(userId, fileId, line, col);
        return ResponseEntity.ok("Saved");
    }

    @GetMapping("/{fileId}/cursor")
    public ResponseEntity<int[]> getCursor(@PathVariable Long fileId, HttpServletRequest request) {
        Long userId = getUserId(request);
        return ResponseEntity.ok(fileService.getCursorPosition(userId, fileId));
    }

    @GetMapping("/project/{projectId}/trash")
    public ResponseEntity<List<ProjectFileDto>> listDeletedFiles(@PathVariable Long projectId) {
        return ResponseEntity.ok(fileService.getDeletedFilesByProject(projectId));
    }

    @PutMapping("/{fileId}/restore")
    public ResponseEntity<String> restoreFile(@PathVariable Long fileId, HttpServletRequest request) {
        Long userId = getUserId(request);
        ProjectFileDto file = fileService.getFileById(fileId);
        checkEditor(userId, file.getProjectId()); // Check "Real" ownership
        fileService.restoreFile(fileId);
        return ResponseEntity.ok("File restored");
    }

    @PutMapping("/{fileId}/versions/{versionId}/restore")
    public ResponseEntity<String> restoreVersion(
            @PathVariable Long fileId,
            @PathVariable Long versionId,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        ProjectFileDto file = fileService.getFileById(fileId);
        checkEditor(userId, file.getProjectId());
        fileService.restoreVersion(fileId, versionId);
        return ResponseEntity.ok("Version restored");
    }
    @MessageMapping("/edit/{fileId}")
    @SendTo("/topic/files/{fileId}")
    public Map<String, Object> broadcastEdit(@DestinationVariable Long fileId, @Payload Map<String, Object> payload) {
        String content = (String) payload.get("content");

        // Save to DB so it doesn't vanish on refresh
        if (content != null) {
            fileService.updateFileContent(fileId, content);
        }
        return payload;
    }

    // 2. Handle Cursors (Live Movement + Remember Position)
    @MessageMapping("/cursor/{fileId}")
    @SendTo("/topic/cursors/{fileId}")
    public Map<String, Object> broadcastCursor(@DestinationVariable Long fileId, @Payload Map<String, Object> payload) {

        // Extract data
        Long userId = ((Number) payload.get("userId")).longValue();
        int line = ((Number) payload.get("lineNumber")).intValue();
        int col = ((Number) payload.get("column")).intValue();

        // Save "Last Stopped" position to DB
        fileService.saveCursorPosition(userId, fileId, line, col);

        // Broadcast to other users immediately
        return payload;
    }



}