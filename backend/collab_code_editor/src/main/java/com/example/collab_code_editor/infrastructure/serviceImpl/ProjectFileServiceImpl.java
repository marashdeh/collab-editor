package com.example.collab_code_editor.infrastructure.serviceImpl;

import com.example.collab_code_editor.core.dto.FileVersionDto;
import com.example.collab_code_editor.core.dto.ProjectFileDto;
import com.example.collab_code_editor.core.exception.FileNotFoundException;
import com.example.collab_code_editor.core.exception.FolderNotFoundException;
import com.example.collab_code_editor.core.model.FileVersion;
import com.example.collab_code_editor.core.model.Folder;
import com.example.collab_code_editor.core.model.Project;
import com.example.collab_code_editor.core.model.ProjectFile;
import com.example.collab_code_editor.core.service.ProjectFileService;
import com.example.collab_code_editor.infrastructure.repository.FileVersionRepository;
import com.example.collab_code_editor.infrastructure.repository.FolderRepository;
import com.example.collab_code_editor.infrastructure.repository.ProjectFileRepository;
import com.example.collab_code_editor.infrastructure.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ProjectFileServiceImpl implements ProjectFileService {

    private final ProjectFileRepository projectFileRepository;
    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;
    private final FileVersionRepository fileVersionRepository;

    @Override
    public ProjectFileDto createFile(ProjectFileDto dto, Long folderId) {

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectFile file = new ProjectFile();
        file.setName(dto.getName());
        file.setContent(dto.getContent());
        file.setFolder(folder);
        file.setProject(project);
        file.setDeleted(false);
        file.setPath(dto.getPath());

        ProjectFile saved = projectFileRepository.save(file);

        return new ProjectFileDto(
                saved.getId(),
                saved.getName(),
                saved.getContent(),
                saved.getFolder().getId(),
                saved.getProject().getId(),
                saved.isDeleted(),
                saved.getPath()
        );
    }

    @Override
    public ProjectFileDto renameFile(Long fileId, String newName) {

        ProjectFile file = getFile(fileId);

        file.setName(newName);
        file.setUpdatedAt(LocalDateTime.now());

        ProjectFile updated = projectFileRepository.save(file);

        return new ProjectFileDto(
                updated.getId(),
                updated.getName(),
                updated.getContent(),
                updated.getFolder().getId(),
                updated.getProject().getId(),
                updated.isDeleted(),
                updated.getPath()
        );
    }

    @Override
    public void deleteFile(Long fileId) {
        ProjectFile file = getFile(fileId);
        file.setDeleted(true);
        file.setUpdatedAt(LocalDateTime.now());
        projectFileRepository.save(file);
    }

    @Override
    public void restoreFile(Long fileId) {
        ProjectFile file = getFile(fileId);
        file.setDeleted(false);
        file.setUpdatedAt(LocalDateTime.now());
        projectFileRepository.save(file);
    }

    @Override
    public List<ProjectFileDto> listFilesByFolder(Long folderId) {

        List<ProjectFile> files = projectFileRepository.findByFolderId(folderId);

        return files.stream()
                .map(f -> new ProjectFileDto(
                        f.getId(),
                        f.getName(),
                        f.getContent(),
                        f.getFolder().getId(),
                        f.getProject().getId(),
                        f.isDeleted(),
                        f.getPath()
                ))
                .toList();
    }

    @Override
    public List<FileVersionDto> listVersions(Long fileId) {

        ProjectFile file = getFile(fileId);

        return file.getVersions().stream()
                .map(v -> new FileVersionDto(
                        v.getId(),
                        v.getVersionNumber(),
                        v.getEditedAt(),
                        v.getContent()
                )).toList();
    }

    @Override
    public void createVersion(ProjectFile file) {

        FileVersion version = new FileVersion();
        version.setFile(file);
        version.setContent(file.getContent());
        version.setVersionNumber(file.getVersions().size() + 1);
        version.setEditedAt(LocalDateTime.now());

        fileVersionRepository.save(version);
    }

    @Override
    public ProjectFileDto updateFileContent(Long fileId, String newContent) {

        ProjectFile file = getFile(fileId);

        file.setContent(newContent);
        file.setUpdatedAt(LocalDateTime.now());

        createVersion(file);

        ProjectFile saved = projectFileRepository.save(file);

        return new ProjectFileDto(
                saved.getId(),
                saved.getName(),
                saved.getContent(),
                saved.getFolder().getId(),
                saved.getProject().getId(),
                saved.isDeleted(),
                saved.getPath()
        );
    }

    @Override
    public ProjectFile getFile(Long fileId) {
        return projectFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

    @Override
    public ProjectFileDto getFileById(Long fileId) {

        ProjectFile file = getFile(fileId);

        return new ProjectFileDto(
                file.getId(),
                file.getName(),
                file.getContent(),
                file.getFolder().getId(),
                file.getProject().getId(),
                file.isDeleted(),
                file.getPath()
        );
    }
}
