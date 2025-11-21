package com.example.collab_code_editor.core.service;

import com.example.collab_code_editor.core.dto.ProjectDto;

import java.util.List;

public interface ProjectService {
    ProjectDto createProject (ProjectDto dto, Long ownerId);
    List<ProjectDto> ListUserProjects (Long ownerId);
    ProjectDto getProjectById(Long id);
    void deleteProject(Long id, Long ownerId);
}
