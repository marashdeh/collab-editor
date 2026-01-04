package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    // Existing method
    Optional<Collaborator> findByUserIdAndProjectId(Long userId, Long projectId);

    // ✅ ADD THIS: Find all projects a user belongs to
    List<Collaborator> findAllByUserId(Long userId);

    // Used for the sidebar list
    List<Collaborator> findAllByProjectId(Long projectId);
}