package com.example.collab_code_editor.infrastructure.serviceImpl;

import com.example.collab_code_editor.core.dto.FolderDto;
import com.example.collab_code_editor.core.exception.FolderNotFoundException;
import com.example.collab_code_editor.core.exception.ProjectNotFoundException;
import com.example.collab_code_editor.core.model.Folder;
import com.example.collab_code_editor.core.model.Project;
import com.example.collab_code_editor.core.service.FolderService;
import com.example.collab_code_editor.infrastructure.repository.FolderRepository;
import com.example.collab_code_editor.infrastructure.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;

    @Override
    public FolderDto createFolder(FolderDto dto, Long projectId, Long parentId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new ProjectNotFoundException("project not found"));
        Folder folder = new Folder();
        folder.setName(dto.getName());
        folder.setProject(project);
        folder.setCreatedAt(LocalDateTime.now());

        if (parentId !=null){
            Folder parent = folderRepository.findById(parentId)
                    .orElseThrow(()-> new RuntimeException("Parent folder not found"));
            folder.setParent(parent);
        }

        folderRepository.save(folder);
        return new FolderDto(folder.getId(), folder.getName(), folder.isDeleted());
    }

    @Override
    public FolderDto renameFolder(Long folderId, String newName) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException("Folder not found"));
        folder.setName(newName);
        folderRepository.save(folder);
        return new FolderDto(folder.getId(), folder.getName(), folder.isDeleted());
    }

    @Override
    public void deleteFolder(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException("Folder not found"));
        folder.setDeleted(true);
        folderRepository.save(folder);
    }

    @Override
    public void restoreFolder(Long folderId) {
            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new FolderNotFoundException("Folder not found"));
            folder.setDeleted(false);
            folderRepository.save(folder);
    }

    @Override
    public List<FolderDto> listFolderByProject(Long projectId) {
        return folderRepository.findByProjectId(projectId)
                .stream()
                .map(f -> new FolderDto(f.getId(), f.getName(), f.isDeleted()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FolderDto> getSubFolders(Long parentId) {
        return folderRepository.findByParentId(parentId)
                .stream()
                .map(f -> new FolderDto(f.getId(),f.getName(),f.isDeleted()))
                .collect(Collectors.toList());
    }
}
