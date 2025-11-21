package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation,Long> {
    List<Invitation> findByProjectId(Long projectId);
    List<Invitation> findByEmail(String email);
}
