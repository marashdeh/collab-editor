package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectFileRepository extends JpaRepository<ProjectFile,Long> {
    List<ProjectFile> findByFolderId(Long folderId);
    List<ProjectFile> findByProjectId(Long projectId);
}
