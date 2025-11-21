package com.example.collab_code_editor.core.service;

import com.example.collab_code_editor.core.model.FileVersion;
import com.example.collab_code_editor.core.model.ProjectFile;

import java.util.List;

public interface FileVersionService {
    void saveNewVersion(ProjectFile file, String content);


}
