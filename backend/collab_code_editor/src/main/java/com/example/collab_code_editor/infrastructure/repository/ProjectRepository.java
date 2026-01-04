package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerId(Long ownerId);

    // ✅ ADD THIS: Check if a project exists by name and owner
    boolean existsByNameAndOwnerId(String name, Long ownerId);
}