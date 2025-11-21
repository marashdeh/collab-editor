package com.example.collab_code_editor.config.security;

import com.example.collab_code_editor.core.exception.UnauthorizedActionException;
import com.example.collab_code_editor.core.exception.UserNotFoundException;
import com.example.collab_code_editor.core.model.Collaborator;
import com.example.collab_code_editor.core.model.CollaboratorRole;
import com.example.collab_code_editor.infrastructure.repository.CollaboratorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessValidator {
    private final CollaboratorRepository collaboratorRepository;

    // Checks if a user is the OWNER of a project
    public void validateOwnerAccess (Long userId, Long projectId){
        Collaborator collaborator =collaboratorRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new UnauthorizedActionException("User not part of this project"));

        if (collaborator.getRole() != CollaboratorRole.OWNER){
            throw new UnauthorizedActionException("Only project owner can perform this action");
        }
    }
    // Checks if a user is either OWNER or MEMBER
    public void validateCollaboratorAccess (Long userId,Long projectId){
        collaboratorRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new UnauthorizedActionException("User not authorized for this project"));
    }
}
