package com.example.collab_code_editor.core.dto;

import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollaboratorUpdateMessage {
    private String type = "COLLAB_UPDATE";
    private Long fileId;
    private List<CollaboratorDto> collaborators;
}