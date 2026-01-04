package com.example.collab_code_editor.presentation.controller;

import com.example.collab_code_editor.core.dto.InvitationDto;
import com.example.collab_code_editor.core.model.User;
import com.example.collab_code_editor.core.security.JwtTokenService;
import com.example.collab_code_editor.core.service.InvitationService;
import com.example.collab_code_editor.infrastructure.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;
    private final JwtTokenService jwt;
    private final UserRepository userRepository;

    private Long getUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer "))
            throw new RuntimeException("Missing or invalid Authorization header");
        return jwt.extractUserId(auth.substring(7));
    }

    @PostMapping("/{projectId}")
    public ResponseEntity<InvitationDto> sendInvitation(
            @PathVariable Long projectId,
            @RequestBody InvitationDto invitationDto,
            HttpServletRequest request) {
        invitationDto.setProjectId(projectId);

        Long userId = getUserId(request);
        invitationDto.setInviterId(userId);

        return ResponseEntity.ok(invitationService.sendInvitation(invitationDto));
    }

    @PutMapping("/{invitationId}")
    public ResponseEntity<InvitationDto> respondToInvitation(
            @PathVariable Long invitationId,
            @RequestParam boolean accept) {
        return ResponseEntity.ok(invitationService.respondToInvitation(invitationId, accept));
    }

    @GetMapping
    public ResponseEntity<List<InvitationDto>> getMyInvitations(HttpServletRequest request) {
        Long userId = getUserId(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<InvitationDto> invitations = invitationService.getInvitationsForUser(user.getEmail());

        return ResponseEntity.ok(invitations);
    }
}