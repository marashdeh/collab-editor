package com.example.collab_code_editor.core.service;

import com.example.collab_code_editor.core.dto.InvitationDto;
import com.example.collab_code_editor.core.model.Invitation;

import java.util.List;

public interface InvitationService {
    InvitationDto sendInvitation(InvitationDto dto);
    InvitationDto respondToInvitation(Long invitationId, boolean accept);
    List<InvitationDto> getInvitationsForUser(String email);
}
