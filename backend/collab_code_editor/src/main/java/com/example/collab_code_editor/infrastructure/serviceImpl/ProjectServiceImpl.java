package com.example.collab_code_editor.infrastructure.serviceImpl;

import com.example.collab_code_editor.core.dto.ProjectDto;
import com.example.collab_code_editor.core.exception.ProjectNotFoundException;
import com.example.collab_code_editor.core.exception.UnauthorizedActionException;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CollaboratorRepository collaboratorRepository;

    @Override
    @Transactional
    public ProjectDto createProject(ProjectDto dto, Long ownerId) {
        //  Check for duplicates first
        if (projectRepository.existsByNameAndOwnerId(dto.getName(), ownerId)) {
            throw new RuntimeException("Project with this name already exists!");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setOwner(owner);
        project.setCreatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);

        Collaborator collaborator = new Collaborator();
        collaborator.setProject(savedProject);
        collaborator.setUser(owner);
        collaborator.setRole(CollaboratorRole.OWNER);
        collaboratorRepository.save(collaborator);

        return new ProjectDto(savedProject.getId(), savedProject.getName(), savedProject.getDescription());
    }

    @Override
    public List<ProjectDto> ListUserProjects(Long userId) {
        return collaboratorRepository.findAllByUserId(userId).stream()
                .map(collaborator -> {
                    Project p = collaborator.getProject();
                    return new ProjectDto(p.getId(), p.getName(), p.getDescription());
                })
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDto getProjectById(Long id) {
        Project p = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        return new ProjectDto(p.getId(), p.getName(), p.getDescription());
    }

    @Override
    @Transactional
    public void deleteProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("There is no project to delete"));

        if (!project.getOwner().getId().equals(userId)) {
            throw new UnauthorizedActionException("Only the owner can delete the project");
        }
        projectRepository.delete(project);
    }
}