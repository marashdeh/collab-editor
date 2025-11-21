package com.example.collab_code_editor.infrastructure.serviceImpl;

import com.example.collab_code_editor.core.dto.InvitationDto;
import com.example.collab_code_editor.core.model.*;
import com.example.collab_code_editor.core.service.InvitationService;
import com.example.collab_code_editor.infrastructure.repository.CollaboratorRepository;
import com.example.collab_code_editor.infrastructure.repository.InvitationRepository;
import com.example.collab_code_editor.infrastructure.repository.ProjectRepository;
import com.example.collab_code_editor.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CollaboratorRepository collaboratorRepository;

    @Override
    public InvitationDto sendInvitation(InvitationDto invitationDto) {
        Long projectId = invitationDto.getProjectId();
        Long inviterId = invitationDto.getInviterId();
        String email = invitationDto.getEmail();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new RuntimeException("Inviter not found"));

        // ✅ Check if inviter is OWNER
        Collaborator collaborator = collaboratorRepository.findByUserIdAndProjectId(inviterId, projectId)
                .orElseThrow(() -> new RuntimeException("Inviter is not part of this project"));
        if (collaborator.getRole() != CollaboratorRole.OWNER)
            throw new RuntimeException("Only project owner can send invitations");

        // ✅ Create the invitation
        Invitation invitation = new Invitation();
        invitation.setEmail(email);
        invitation.setInviter(inviter);
        invitation.setProject(project);
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setSentAt(LocalDateTime.now());
        invitationRepository.save(invitation);

        return toDto(invitation);
    }

    @Override
    public InvitationDto respondToInvitation(Long invitationId, boolean accept) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        if (accept) {
            if (invitation.getStatus() != InvitationStatus.PENDING)
                throw new RuntimeException("Invitation already processed");

            invitation.setStatus(InvitationStatus.ACCEPTED);

            User invitedUser = userRepository.findByEmail(invitation.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Collaborator collaborator = new Collaborator();
            collaborator.setUser(invitedUser);
            collaborator.setProject(invitation.getProject());
            collaborator.setRole(CollaboratorRole.MEMBER);
            collaboratorRepository.save(collaborator);
        } else {
            invitation.setStatus(InvitationStatus.REJECTED);
        }

        invitationRepository.save(invitation);
        return toDto(invitation);
    }

    @Override
    public List<InvitationDto> getInvitationsForUser(String email) {
        return invitationRepository.findByEmail(email)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private InvitationDto toDto(Invitation invitation) {
        return InvitationDto.builder()
                .id(invitation.getId()) // ✅ fixed
                .email(invitation.getEmail())
                .status(invitation.getStatus())
                .sentAt(invitation.getSentAt())
                .projectId(invitation.getProject().getId())
                .projectName(invitation.getProject().getName())
                .inviterId(invitation.getInviter().getId())
                .inviterName(invitation.getInviter().getUsername())
                .build();
    }
}
