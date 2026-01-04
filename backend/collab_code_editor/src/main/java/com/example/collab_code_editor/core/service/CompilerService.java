package com.example.collab_code_editor.core.service;

import com.example.collab_code_editor.core.dto.ExecuteRequest;
import com.example.collab_code_editor.core.dto.ExecuteResponse;

public interface CompilerService {
    ExecuteResponse execute(ExecuteRequest request);
}