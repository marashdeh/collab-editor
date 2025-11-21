package com.example.collab_code_editor.core.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursorUpdateMessage {
    private String type = "CURSOR";
    private Long fileId;
    private int line;
    private int column;
    private String user;
}