package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.Invitation;
import com.example.collab_code_editor.core.model.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    // ✅ THIS LOADS BOB'S INVITES
    List<Invitation> findByEmailAndStatus(String email, InvitationStatus status);

    Optional<Invitation> findByToken(String token);
}