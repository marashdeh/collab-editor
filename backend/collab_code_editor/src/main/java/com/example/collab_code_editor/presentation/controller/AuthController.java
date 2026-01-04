package com.example.collab_code_editor.presentation.controller;

import com.example.collab_code_editor.core.model.User;
import com.example.collab_code_editor.core.security.JwtTokenService;
import com.example.collab_code_editor.core.dto.LoginRequest;
import com.example.collab_code_editor.core.dto.RegisterRequest;
import com.example.collab_code_editor.core.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth") // Matches SecurityConfig
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 1. Authenticate (will throw 403/401 if password wrong)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Cast to your custom User entity to get the ID
        User user = (User) authentication.getPrincipal();

        // 3. Generate Token (Token + ID + 24 hours validity)
        String jwt = jwtTokenService.generateAccessToken(user.getEmail(), user.getId(), 86400000);

        // 4. Return Response with ID as a string
        return ResponseEntity.ok(Map.of(
                "token", jwt,
                "email", user.getEmail(),
                "id", user.getId().toString()
        ));
    }
}