package com.example.collab_code_editor.core.service;

import com.example.collab_code_editor.core.dto.LoginRequest;
import com.example.collab_code_editor.core.dto.RegisterRequest;
import com.example.collab_code_editor.core.dto.UserDto;

public interface AuthService {
    UserDto register(RegisterRequest request);
    String login (LoginRequest request);
}
