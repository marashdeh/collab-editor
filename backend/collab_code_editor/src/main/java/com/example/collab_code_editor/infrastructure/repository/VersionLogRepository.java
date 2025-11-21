package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.VersionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionLogRepository extends JpaRepository<VersionLog,Long> {
}
