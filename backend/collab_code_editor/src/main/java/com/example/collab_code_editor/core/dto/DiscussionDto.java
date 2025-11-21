package com.example.collab_code_editor.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscussionDto {
    private Long id;
    private String topic;
    private String creatorName;
    private LocalDateTime createdAt;
}
