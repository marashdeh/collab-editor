package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.Collaborator;
import com.example.collab_code_editor.core.model.CollaboratorRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CollaboratorRepository extends JpaRepository<Collaborator,Long> {
    // find all collaborators within a project
    List<Collaborator> findByProjectId(Long projectId);

    // Check if a specific user is a collaborator in a project
    Optional<Collaborator> findByUserIdAndProjectId(Long userId,Long projectId );

    @Query("SELECT c.role FROM Collaborator c WHERE c.user.id = :userId AND c.project.id = :projectId")
    Optional<CollaboratorRole> findRoleByUserIdAndProjectId(Long userId, Long projectId);
}
