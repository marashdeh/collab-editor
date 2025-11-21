package com.example.collab_code_editor.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursorMessage {
    private String type;
    private Long fileId;
    private Long userId;
    private int line;
    private int column;
    private String color;
}
