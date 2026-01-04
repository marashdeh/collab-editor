package com.example.collab_code_editor.infrastructure.serviceImpl;

import com.example.collab_code_editor.core.dto.CommentDto;
import com.example.collab_code_editor.core.exception.UserNotFoundException;
import com.example.collab_code_editor.core.model.Comment;
import com.example.collab_code_editor.core.model.Discussion;
import com.example.collab_code_editor.core.model.User;
import com.example.collab_code_editor.core.service.CommentService;
import com.example.collab_code_editor.infrastructure.repository.CommentRepository;
import com.example.collab_code_editor.infrastructure.repository.DiscussionRepository;
import com.example.collab_code_editor.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final DiscussionRepository discussionRepository;
    private final UserRepository userRepository;

    @Override
    public CommentDto addComment(Long discussionId, Long userId, String content) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setDiscussion(discussion);
        comment.setAuthor(user);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);

        return new CommentDto(
                saved.getId(),
                saved.getContent(),
                saved.getAuthor().getUsername(),
                saved.getCreatedAt()
        );
    }

    @Override
    public List<CommentDto> getCommentsByDiscussion(Long discussionId) {
        return commentRepository.findByDiscussionIdOrderByCreatedAtAsc(discussionId).stream()
                .map(c -> new CommentDto(
                        c.getId(),
                        c.getContent(),
                        c.getAuthor().getUsername(),
                        c.getCreatedAt()
                ))
                .toList();
    }
}