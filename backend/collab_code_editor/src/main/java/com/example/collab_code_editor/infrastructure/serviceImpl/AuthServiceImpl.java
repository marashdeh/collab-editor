package com.example.collab_code_editor.infrastructure.serviceImpl;


import com.example.collab_code_editor.core.dto.LoginRequest;
import com.example.collab_code_editor.core.dto.RegisterRequest;
import com.example.collab_code_editor.core.dto.UserDto;
import com.example.collab_code_editor.core.exception.UserNotFoundException;
import com.example.collab_code_editor.core.model.User;
import com.example.collab_code_editor.core.security.JwtTokenService;
import com.example.collab_code_editor.core.service.AuthService;
import com.example.collab_code_editor.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtTokenService jwtTokenService;

    @Override
    public UserDto register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }

    @Override
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid password");
        }


        return jwtTokenService.generateAccessToken(
                user.getEmail(),  // Subject (sub)
                user.getId(),     // Claim (userId)
                86400000          // 1 day expiration
        );
    }
}