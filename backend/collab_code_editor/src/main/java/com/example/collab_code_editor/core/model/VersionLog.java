package com.example.collab_code_editor.core.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "version_logs")
public class VersionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "file_version_id")
    private FileVersion fileVersion;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
