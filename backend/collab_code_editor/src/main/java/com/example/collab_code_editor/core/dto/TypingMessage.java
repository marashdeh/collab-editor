package com.example.collab_code_editor.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypingMessage {
    private String type;     // "TYPING"
    private Long fileId;
    private Long userId;
}