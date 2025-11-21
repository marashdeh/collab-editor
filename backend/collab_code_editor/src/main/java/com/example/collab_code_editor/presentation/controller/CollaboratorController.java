package com.example.collab_code_editor.presentation.controller;

import com.example.collab_code_editor.core.dto.CollaboratorDto;
import com.example.collab_code_editor.core.service.CollaboratorService;
import com.example.collab_code_editor.infrastructure.serviceImpl.CollaboratorServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collaborators")
public class CollaboratorController {

    private final CollaboratorService collaboratorService;

    //  Direct add disabled (now handled through Invitation acceptance)

    // @PostMapping("/{projectId}/{userId}")
    // public ResponseEntity<CollaboratorDto> addCollaborator(@PathVariable Long projectId,
    //                                                        @PathVariable Long userId,
    //                                                        @RequestParam(defaultValue = "MEMBER") String role) {
    //     throw new UnsupportedOperationException("Collaborators must join through accepted invitations");
    // }

    // Get all collaborators in a project
    @GetMapping("/{projectId}")
    public ResponseEntity<List<CollaboratorDto>> getCollaborators(@PathVariable Long projectId) {
        return ResponseEntity.ok(collaboratorService.getCollaboratorsByProject(projectId));
    }
}