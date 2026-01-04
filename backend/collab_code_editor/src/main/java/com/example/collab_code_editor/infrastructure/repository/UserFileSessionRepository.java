package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.UserFileSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserFileSessionRepository extends JpaRepository<UserFileSession, Long> {
    Optional<UserFileSession> findByUserIdAndFileId(Long userId, Long fileId);
}