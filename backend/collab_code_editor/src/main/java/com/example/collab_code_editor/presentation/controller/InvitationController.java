package com.example.collab_code_editor.presentation.controller;

import com.example.collab_code_editor.core.dto.InvitationDto;
import com.example.collab_code_editor.core.model.Invitation;
import com.example.collab_code_editor.core.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/{projectId}")
    public ResponseEntity<InvitationDto> sendInvitation(
            @PathVariable Long projectId,
            @RequestBody InvitationDto invitationDto) {
        // Ensure projectId is set before sending
        invitationDto.setProjectId(projectId);
        return ResponseEntity.ok(invitationService.sendInvitation(invitationDto));
    }

    // ✅ Accept or reject invitation
    @PutMapping("/{invitationId}")
    public ResponseEntity<InvitationDto> respondToInvitation(
            @PathVariable Long invitationId,
            @RequestParam boolean accept) {
        return ResponseEntity.ok(invitationService.respondToInvitation(invitationId, accept));
    }

    // ✅ Get all invitations for a specific user (by email)
    @GetMapping
    public ResponseEntity<List<InvitationDto>> getInvitationsForUser(@RequestParam String email) {
        List<InvitationDto> invitations = invitationService.getInvitationsForUser(email);
        return ResponseEntity.ok(invitations);
    }
}
