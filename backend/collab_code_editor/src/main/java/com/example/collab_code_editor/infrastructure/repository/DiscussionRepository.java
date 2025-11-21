package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscussionRepository extends JpaRepository<Discussion,Long> {
    List<Discussion> findByFileId(Long fileId);
}
