package com.example.collab_code_editor.core.service;

import com.example.collab_code_editor.core.dto.CollaboratorDto;
import com.example.collab_code_editor.core.model.CollaboratorRole;

import java.util.List;

public interface CollaboratorService {

    CollaboratorDto addCollaborator(Long projectId, Long userId, CollaboratorRole role);

    List<CollaboratorDto> getCollaboratorsByProject(Long projectId);

    CollaboratorRole getRoleForUserAndProject(Long userId, Long projectId);
}
