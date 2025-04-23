package com.devteria.identityservice.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class ChatMessage {
    @Id
    private String id;

    private String chatId;
    private String senderId;
    private String recipientId;
    private String content;
    private LocalDate timestamp;
}
