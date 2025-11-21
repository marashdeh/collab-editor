package com.example.collab_code_editor.core.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditorChangeMessage {
    private Long fileId;
    private String type = "EDIT";
    private String content;
}
