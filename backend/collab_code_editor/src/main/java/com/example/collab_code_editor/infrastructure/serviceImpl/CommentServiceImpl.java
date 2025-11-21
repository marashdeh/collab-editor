package com.example.collab_code_editor.infrastructure.serviceImpl;

import com.example.collab_code_editor.core.dto.CommentDto;
import com.example.collab_code_editor.core.model.Comment;
import com.example.collab_code_editor.core.model.Discussion;
import com.example.collab_code_editor.core.model.User;
import com.example.collab_code_editor.core.service.CommentService;
import com.example.collab_code_editor.infrastructure.repository.CommentRepository;
import com.example.collab_code_editor.infrastructure.repository.DiscussionRepository;
import com.example.collab_code_editor.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setDiscussion(discussion);
        comment.setAuthor(author);
        comment.setContent(content);
        commentRepository.save(comment);

        return new CommentDto(comment.getId(), content, author.getUsername(), comment.getCreatedAt());
    }


    @Override
    public List<CommentDto> getCommentsByDiscussion(Long discussionId) {
        return commentRepository.findByDiscussionId(discussionId)
                .stream()
                .map(c -> new CommentDto(c.getId(), c.getContent(), c.getAuthor().getUsername(), c.getCreatedAt()))
                .toList();
    }

}
