package com.example.collab_code_editor.core.dto;

import com.example.collab_code_editor.core.model.CollaboratorRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaboratorDto {
    private Long id;
    private Long userId;
    private String username;
    private CollaboratorRole role;
    private String color;
}