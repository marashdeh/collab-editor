package com.example.collab_code_editor.infrastructure.serviceImpl;

import com.example.collab_code_editor.core.dto.FileVersionDto;
import com.example.collab_code_editor.core.dto.ProjectFileDto;
import com.example.collab_code_editor.core.exception.FileNotFoundException;
import com.example.collab_code_editor.core.exception.FolderNotFoundException;
import com.example.collab_code_editor.core.model.*;
import com.example.collab_code_editor.core.service.ProjectFileService;
import com.example.collab_code_editor.infrastructure.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectFileServiceImpl implements ProjectFileService {

    private final UserFileSessionRepository sessionRepository;
    private final ProjectFileRepository projectFileRepository;
    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;
    private final FileVersionRepository fileVersionRepository;
    private final UserRepository userRepository;

    @Override
    public ProjectFileDto createFile(ProjectFileDto dto, Long folderId) {
        Folder folder = null;
        if (folderId != null) {
            folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new FolderNotFoundException("Folder not found"));
        }

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));


        ProjectFile file = new ProjectFile();
        file.setName(dto.getName());
        file.setContent(dto.getContent());
        file.setFolder(folder); // This will be null if created at root, which is correct
        file.setProject(project);
        file.setDeleted(false);
        file.setPath(dto.getPath());

        ProjectFile saved = projectFileRepository.save(file);

        return mapToDto(saved);
    }

    @Override
    public ProjectFileDto renameFile(Long fileId, String newName) {
        ProjectFile file = getFile(fileId);
        file.setName(newName);
        file.setUpdatedAt(LocalDateTime.now());
        ProjectFile updated = projectFileRepository.save(file);
        return mapToDto(updated);
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
        ProjectFile file = projectFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        file.setDeleted(false); // Restore
        projectFileRepository.save(file);
    }

    @Override
    public List<ProjectFileDto> listFilesByFolder(Long folderId) {
        List<ProjectFile> files = projectFileRepository.findByFolderId(folderId);
        return files.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<ProjectFileDto> getFilesByProject(Long projectId) {
        List<ProjectFile> files = projectFileRepository.findByProjectIdAndDeletedFalse(projectId);
        return files.stream().map(this::mapToDto).toList();
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

        int versionNumber = (file.getVersions() == null) ? 1 : file.getVersions().size() + 1;

        version.setVersionNumber(versionNumber);
        version.setEditedAt(LocalDateTime.now());
        fileVersionRepository.save(version);
    }

    @Override
    @Transactional
    public ProjectFileDto updateFileContent(Long fileId, String newContent) {
        ProjectFile file = getFile(fileId);
        file.setContent(newContent);
        file.setUpdatedAt(LocalDateTime.now());
        createVersion(file);
        ProjectFile saved = projectFileRepository.save(file);
        return mapToDto(saved);
    }

    @Override
    public ProjectFile getFile(Long fileId) {
        return projectFileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));
    }

    @Override
    public ProjectFileDto getFileById(Long fileId) {
        return mapToDto(getFile(fileId));
    }

    private ProjectFileDto mapToDto(ProjectFile file) {
        return new ProjectFileDto(
                file.getId(),
                file.getName(),
                file.getContent(),
                (file.getFolder() != null) ? file.getFolder().getId() : null,
                file.getProject().getId(),
                file.isDeleted(),
                file.getPath()
        );
    }
    @Override
    public void saveCursorPosition(Long userId, Long fileId, int line, int col) {
        UserFileSession session = sessionRepository.findByUserIdAndFileId(userId, fileId)
                .orElse(new UserFileSession());

        if (session.getId() == null) {
            session.setUser(userRepository.findById(userId).orElseThrow());
            session.setFile(projectFileRepository.findById(fileId).orElseThrow());
        }

        session.setLastLine(line);
        session.setLastColumn(col);
        sessionRepository.save(session);
    }

    @Override
    public int[] getCursorPosition(Long userId, Long fileId) {
        return sessionRepository.findByUserIdAndFileId(userId, fileId)
                .map(s -> new int[]{s.getLastLine(), s.getLastColumn()})
                .orElse(new int[]{1, 1}); // Default to line 1, col 1
    }
    @Override
    public List<ProjectFileDto> getDeletedFilesByProject(Long projectId) {
        // uses the new repository method for deleted=true
        List<ProjectFile> files = projectFileRepository.findByProjectIdAndDeletedTrue(projectId);
        return files.stream().map(this::mapToDto).toList();
    }


    @Override
    @Transactional
    public void restoreVersion(Long fileId, Long versionId) {
        ProjectFile file = getFile(fileId);
        FileVersion oldVersion = fileVersionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));

        // revert content
        file.setContent(oldVersion.getContent());
        file.setUpdatedAt(LocalDateTime.now());

        // create new version for this revert
        createVersion(file);

        projectFileRepository.save(file);
    }
}