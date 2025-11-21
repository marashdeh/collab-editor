package com.example.collab_code_editor.core.service;

import com.example.collab_code_editor.core.dto.FileVersionDto;
import com.example.collab_code_editor.core.dto.ProjectFileDto;
import com.example.collab_code_editor.core.model.ProjectFile;

import java.util.List;

public interface ProjectFileService {
    ProjectFileDto createFile(ProjectFileDto dto, Long folderId);
    ProjectFileDto renameFile(Long fileId , String newName);
    void deleteFile(Long fileId);
    void restoreFile(Long fileId);
    List<ProjectFileDto> listFilesByFolder(Long folderId);
    List<FileVersionDto> listVersions(Long fileId);
    void createVersion(ProjectFile file);
    ProjectFileDto updateFileContent(Long fileId,String newContent);
    ProjectFile getFile(Long fileId);
    ProjectFileDto getFileById(Long fileId);

}
