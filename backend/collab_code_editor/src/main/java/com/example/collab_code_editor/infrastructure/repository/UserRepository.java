package com.example.collab_code_editor.infrastructure.repository;

import com.example.collab_code_editor.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional <User> findByEmail(String email);
    Optional <User> findById(Long ownerId);

}
