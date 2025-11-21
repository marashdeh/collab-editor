package com.example.collab_code_editor.infrastructure.serviceImpl;

import com.example.collab_code_editor.core.dto.CollaboratorDto;
import com.example.collab_code_editor.core.model.CollaboratorRole;
import com.example.collab_code_editor.core.service.CollaboratorService;
import com.example.collab_code_editor.core.util.ColorUtil;
import com.example.collab_code_editor.infrastructure.repository.CollaboratorRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.collab_code_editor.core.util.ColorUtil.generateColor;

@Service
@RequiredArgsConstructor
public class CollaboratorServiceImpl implements CollaboratorService {

    private final CollaboratorRepository collaboratorRepository;

    @Override
    public CollaboratorDto addCollaborator(Long projectId, Long userId, CollaboratorRole role) {
        throw new UnsupportedOperationException("Collaborators must join through accepted invitations");
    }

    @Override
    public List<CollaboratorDto> getCollaboratorsByProject(Long projectId) {
        return collaboratorRepository.findByProjectId(projectId)
                .stream()
                .map(c -> CollaboratorDto.builder()
                        .id(c.getId())
                        .userId(c.getUser().getId())
                        .username(c.getUser().getUsername())
                        .role(c.getRole())
                        .color(ColorUtil.generateColor(c.getUser().getId()))
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public CollaboratorRole getRoleForUserAndProject(Long userId, Long projectId) {
        return collaboratorRepository.findRoleByUserIdAndProjectId(userId, projectId)
                .orElse(CollaboratorRole.MEMBER);
    }
}
