package com.example.collab_code_editor.infrastructure.serviceImpl;

import com.example.collab_code_editor.core.model.FileVersion;
import com.example.collab_code_editor.core.model.ProjectFile;
import com.example.collab_code_editor.core.service.FileVersionService;
import com.example.collab_code_editor.infrastructure.repository.FileVersionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileVersionServiceImpl implements FileVersionService {

    private final FileVersionRepository fileVersionRepository;

    @Override
    @Transactional
    public void saveNewVersion(ProjectFile file, String content) {

        // Find next version number
        int nextVersion = fileVersionRepository.findMaxVersionNumber(file.getId()) + 1;

        FileVersion version = new FileVersion();
        version.setFile(file);
        version.setContent(content);
        version.setVersionNumber(nextVersion);

        fileVersionRepository.save(version);
    }
}
