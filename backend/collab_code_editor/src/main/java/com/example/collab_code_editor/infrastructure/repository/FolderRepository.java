package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByProjectId(Long projectId);
    List<Folder> findByParentId(Long parentId);
}
