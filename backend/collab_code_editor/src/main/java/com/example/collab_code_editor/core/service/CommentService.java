package com.example.collab_code_editor.core.service;

import com.example.collab_code_editor.core.dto.CommentDto;
import java.util.List;

public interface CommentService {
    CommentDto addComment(Long discussionId, Long userId, String content);
    List<CommentDto> getCommentsByDiscussion(Long discussionId);
}