package com.ai.chat.ChatRepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai.chat.models.AppUser;

public interface UserRepository
        extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);
}