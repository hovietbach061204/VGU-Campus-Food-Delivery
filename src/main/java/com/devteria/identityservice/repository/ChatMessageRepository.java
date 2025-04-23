package com.devteria.identityservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devteria.identityservice.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByChatId(String chatId);
}
