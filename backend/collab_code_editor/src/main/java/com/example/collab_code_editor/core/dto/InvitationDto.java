package com.example.collab_code_editor.core.dto;

import com.example.collab_code_editor.core.model.InvitationStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationDto {
    private Long id;
    @NotBlank
    @Email
    private String email;
    private InvitationStatus status;
    private LocalDateTime sentAt;
    private Long projectId;
    private String projectName;
    private Long inviterId;
    private String inviterName;
}