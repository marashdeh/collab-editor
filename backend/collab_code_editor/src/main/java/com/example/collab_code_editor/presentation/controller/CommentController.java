package com.example.collab_code_editor.presentation.controller;

import com.example.collab_code_editor.core.dto.CommentDto;
import com.example.collab_code_editor.core.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{discussionId}/{userId}")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long discussionId,
            @PathVariable Long userId,
            @RequestParam String content) {
        return ResponseEntity.ok(commentService.addComment(discussionId, userId, content));
    }

    @GetMapping("/{discussionId}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long discussionId) {
        return ResponseEntity.ok(commentService.getCommentsByDiscussion(discussionId));
    }
}
