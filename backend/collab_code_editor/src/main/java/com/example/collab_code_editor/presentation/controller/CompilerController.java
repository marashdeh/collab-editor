package com.example.collab_code_editor.presentation.controller;

import com.example.collab_code_editor.core.dto.ExecuteRequest;
import com.example.collab_code_editor.core.dto.ExecuteResponse;
import com.example.collab_code_editor.core.service.CompilerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compiler")
@RequiredArgsConstructor
public class CompilerController {

    private final CompilerService compilerService;

    @PostMapping("/run")
    public ResponseEntity<ExecuteResponse> runCode(@RequestBody ExecuteRequest request) {
        return ResponseEntity.ok(compilerService.execute(request));
    }
}