package com.example.collab_code_editor.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFileDto {
    private Long id;
    @NotBlank
    @Pattern(regexp = "^[^\\\\:*?\"<>|]+$", message="Invalid file name")
    private String name;
    private String content;

    private Long folderId;
    private Long projectId;

    private boolean deleted;
    private String path;
}

