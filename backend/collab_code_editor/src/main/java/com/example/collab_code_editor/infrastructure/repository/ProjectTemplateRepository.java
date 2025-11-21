package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.ProjectTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectTemplateRepository extends JpaRepository<ProjectTemplate,Long> {
}
