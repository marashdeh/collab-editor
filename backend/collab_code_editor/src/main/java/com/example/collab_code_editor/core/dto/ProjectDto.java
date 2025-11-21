package com.example.collab_code_editor.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long id;
    @NotBlank(message = "name is required")
    @Size(max =80)
    private String name;
    @Size(max = 500)
    private String description;
}
