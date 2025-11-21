package com.example.collab_code_editor.core.service;

import com.example.collab_code_editor.core.dto.DiscussionDto;

import java.util.List;

public interface DiscussionService {
    DiscussionDto createDiscussion (Long fileId, Long userId, String topic);
    List<DiscussionDto> getDiscussionByFile(Long fileId);
}
