package com.example.collab_code_editor.core.service;

import com.example.collab_code_editor.core.dto.FileVersionDto;
import com.example.collab_code_editor.core.dto.ProjectFileDto;
import com.example.collab_code_editor.core.model.ProjectFile;

import java.util.List;

public interface ProjectFileService {
    ProjectFileDto createFile(ProjectFileDto dto, Long folderId);
    ProjectFileDto renameFile(Long fileId , String newName);
    void deleteFile(Long fileId);
    List<ProjectFileDto> getDeletedFilesByProject(Long projectId);
    List<ProjectFileDto> listFilesByFolder(Long folderId);
    List<FileVersionDto> listVersions(Long fileId);
    void createVersion(ProjectFile file);
    ProjectFileDto updateFileContent(Long fileId,String newContent);
    ProjectFile getFile(Long fileId);
    ProjectFileDto getFileById(Long fileId);
    List<ProjectFileDto> getFilesByProject(Long projectId);
    void saveCursorPosition(Long userId, Long fileId, int line, int col);
    int[] getCursorPosition(Long userId, Long fileId);
    void restoreVersion(Long fileId, Long versionId);
    void restoreFile(Long fileId);
}
