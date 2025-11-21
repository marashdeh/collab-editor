package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerId (Long ownerId);
}
