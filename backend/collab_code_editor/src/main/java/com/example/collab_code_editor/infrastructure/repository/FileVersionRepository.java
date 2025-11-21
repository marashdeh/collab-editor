package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.FileVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileVersionRepository extends JpaRepository<FileVersion,Long> {
    List<FileVersion> findByFileIdOrderByVersionNumberDesc(Long fileId);

    @Query("SELECT COALESCE(MAX(v.versionNumber), 0) FROM FileVersion v WHERE v.file.id = :fileId")
    int findMaxVersionNumber(@Param("fileId") Long fileId);
}
