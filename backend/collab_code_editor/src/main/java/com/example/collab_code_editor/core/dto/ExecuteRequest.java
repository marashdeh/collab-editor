package com.example.collab_code_editor.core.dto;

import lombok.Data;

@Data
public class ExecuteRequest {
    private String language; // "java", "python", "cpp"
    private String code;
}