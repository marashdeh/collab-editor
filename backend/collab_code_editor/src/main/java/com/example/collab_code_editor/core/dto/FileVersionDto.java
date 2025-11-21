package com.example.collab_code_editor.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileVersionDto {
    private Long id;
    private int versionNumber;
    private LocalDateTime editedAt;
    private String content;
}
