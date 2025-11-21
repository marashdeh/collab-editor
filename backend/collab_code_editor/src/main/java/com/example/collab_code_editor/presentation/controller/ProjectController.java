package com.example.collab_code_editor.presentation.controller;

import com.example.collab_code_editor.core.dto.ProjectDto;
import com.example.collab_code_editor.infrastructure.serviceImpl.ProjectServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectServiceImpl projectService;

    @PostMapping("/{ownerId}")
    public ResponseEntity<ProjectDto> createProject(
            @PathVariable Long ownerId,
            @RequestBody ProjectDto dto) {
        return ResponseEntity.ok(projectService.createProject(dto, ownerId));
    }

    // List all projects owned by a specific user

    @GetMapping("/{ownerId}")
    public ResponseEntity<List<ProjectDto>> getUserProjects(@PathVariable Long ownerId) {
        return ResponseEntity.ok(projectService.ListUserProjects(ownerId));
    }

    // Get single project details

    @GetMapping("/details/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    // Delete a project (only owner)

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(
            @PathVariable Long id,
            @RequestParam Long userId) {
        projectService.deleteProject(id, userId);
        return ResponseEntity.ok("Project deleted successfully");
    }
}

