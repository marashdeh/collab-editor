package com.example.collab_code_editor.core.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentJoinMessage {
    private String type = "JOIN";
    private Long fileId;
}