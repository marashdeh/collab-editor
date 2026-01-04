package com.example.collab_code_editor.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExecuteResponse {
    private String output;
    private boolean isError;
}