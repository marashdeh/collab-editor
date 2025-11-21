package com.example.collab_code_editor.presentation.controller;

import com.example.collab_code_editor.core.dto.DiscussionDto;
import com.example.collab_code_editor.core.service.DiscussionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/discussions")
public class DiscussionController {
    private final DiscussionService discussionService;

    @PostMapping("/{fileId}/{userId}")
    public ResponseEntity<DiscussionDto> createDiscussion(
            @PathVariable Long fileId,
            @PathVariable Long userId,
            @RequestParam String topic) {
        return ResponseEntity.ok(discussionService.createDiscussion(fileId, userId, topic));
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<List<DiscussionDto>> getDiscussionsByFile(@PathVariable Long fileId) {
        return ResponseEntity.ok(discussionService.getDiscussionByFile(fileId));
    }
}
