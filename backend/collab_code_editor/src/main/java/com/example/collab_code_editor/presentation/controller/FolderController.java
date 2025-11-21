package com.example.collab_code_editor.presentation.controller;


import com.example.collab_code_editor.core.dto.FolderDto;
import com.example.collab_code_editor.core.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;

    //   Create a folder (optionally inside a parent)
    @PostMapping("/{projectId}")
    public ResponseEntity<FolderDto> createFolder(
            @PathVariable Long projectId,
            @RequestParam(required = false) Long parentId,
            @RequestBody FolderDto dto) {
        return ResponseEntity.ok(folderService.createFolder(dto, projectId, parentId));
    }

    //  Rename
    @PutMapping("/{folderId}")
    public ResponseEntity<FolderDto> renameFolder(
            @PathVariable Long folderId,
            @RequestParam String newName) {
        return ResponseEntity.ok(folderService.renameFolder(folderId, newName));
    }

    //  Soft delete
    @DeleteMapping("/{folderId}")
    public ResponseEntity<String> deleteFolder(@PathVariable Long folderId) {
        folderService.deleteFolder(folderId);
        return ResponseEntity.ok("Folder deleted (soft)");
    }

    //  Restore
    @PutMapping("/{folderId}/restore")
    public ResponseEntity<String> restoreFolder(@PathVariable Long folderId) {
        folderService.restoreFolder(folderId);
        return ResponseEntity.ok("Folder restored");
    }

    // List all folders in a project
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<FolderDto>> listFoldersByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(folderService.listFolderByProject(projectId));
    }

    @GetMapping("/sub/{parenttId}")
    public ResponseEntity<List<FolderDto>> getSubFolders(@PathVariable Long parentId){
        return ResponseEntity.ok(folderService.getSubFolders(parentId));
    }
}
