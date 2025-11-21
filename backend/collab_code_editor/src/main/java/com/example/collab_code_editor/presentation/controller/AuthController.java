package com.example.collab_code_editor.presentation.controller;

import com.example.collab_code_editor.core.dto.LoginRequest;
import com.example.collab_code_editor.core.dto.RegisterRequest;
import com.example.collab_code_editor.core.dto.UserDto;
import com.example.collab_code_editor.core.security.JwtTokenService;
import com.example.collab_code_editor.core.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtTokenService jwt;


    @PostMapping("/register")
    public ResponseEntity<UserDto> register (@RequestBody RegisterRequest request){
        UserDto newUser = authService.register(request);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login (@RequestBody LoginRequest request){
        String result = authService.login(request);
        return ResponseEntity.ok(result);
    }
    public static record LoginReq(String email, String password) {}
}

