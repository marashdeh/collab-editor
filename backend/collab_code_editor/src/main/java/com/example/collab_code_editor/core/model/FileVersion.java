package com.example.collab_code_editor.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file_versions")

public class FileVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int versionNumber;
    private LocalDateTime editedAt;

    @Lob
    private String content;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private ProjectFile file;


    @PrePersist
    public void onCreate() {
        if (editedAt == null) {
            editedAt = LocalDateTime.now();
        }
    }


}
