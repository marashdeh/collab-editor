package com.example.collab_code_editor.core.service;

import com.example.collab_code_editor.core.dto.FolderDto;

import java.util.List;

public interface FolderService {
    FolderDto createFolder (FolderDto folderDto, Long projectId, Long parentId);
    FolderDto renameFolder (Long folderId , String newName);
    void deleteFolder (Long folderId);
    void restoreFolder (Long folderId);
    List<FolderDto> listFolderByProject (Long projectId);
    List<FolderDto> getSubFolders(Long parentId);
}
