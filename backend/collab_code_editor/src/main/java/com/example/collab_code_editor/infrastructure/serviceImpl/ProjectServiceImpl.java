package com.example.collab_code_editor.infrastructure.serviceImpl;

import com.example.collab_code_editor.config.security.AccessValidator;
import com.example.collab_code_editor.core.dto.ProjectDto;
import com.example.collab_code_editor.core.exception.ProjectNotFoundException;
import com.example.collab_code_editor.core.exception.UserNotFoundException;
import com.example.collab_code_editor.core.model.Collaborator;
import com.example.collab_code_editor.core.model.CollaboratorRole;
import com.example.collab_code_editor.core.model.Project;
import com.example.collab_code_editor.core.model.User;
import com.example.collab_code_editor.core.service.ProjectService;
import com.example.collab_code_editor.infrastructure.repository.CollaboratorRepository;
import com.example.collab_code_editor.infrastructure.repository.ProjectRepository;
import com.example.collab_code_editor.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final CollaboratorRepository collaboratorRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AccessValidator accessValidator;

    @Override
    public ProjectDto createProject(ProjectDto dto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setOwner(owner);
        project.setCreatedAt(LocalDateTime.now());
        projectRepository.save(project);

        Collaborator ownerCollaborator = new Collaborator();
        ownerCollaborator.setProject(project);
        ownerCollaborator.setUser(owner);
        ownerCollaborator.setRole(CollaboratorRole.OWNER);
        collaboratorRepository.save(ownerCollaborator);

        return new ProjectDto(project.getId(),project.getName(), project.getDescription());
    }

    @Override
    public List<ProjectDto> ListUserProjects(Long ownerId) {
        return projectRepository.findByOwnerId(ownerId)
                .stream()
                .map(p -> new ProjectDto(p.getId(), p.getName(), p.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDto getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        return new ProjectDto(project.getId(), project.getName(), project.getDescription());
    }

    @Override
    public void deleteProject(Long projectId, Long userId) {
        accessValidator.validateOwnerAccess(userId, projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        projectRepository.delete(project);
    }

}
