package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {
    // Queries used by the File Service
    List<ProjectFile> findByFolderId(Long folderId);

    //   LOADS YOUR FILES
    List<ProjectFile> findByProjectIdAndDeletedFalse(Long projectId);
    List<ProjectFile> findByProjectIdAndDeletedTrue(Long projectId);
}
