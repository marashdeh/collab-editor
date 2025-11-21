package com.example.collab_code_editor.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectFileContentDto {
    private Long id;
    private String name;
    private String content;
}