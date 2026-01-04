package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDiscussionIdOrderByCreatedAtAsc(Long discussionId);
}