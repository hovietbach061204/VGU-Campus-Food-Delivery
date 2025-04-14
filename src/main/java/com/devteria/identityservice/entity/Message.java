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
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String messages;

    String text;
    LocalDate timestamp;

//    @ManyToOne
//    @JoinColumn(name = "chatId")
//    ChatRoom chatRoom;
//
//    @OneToOne
//    @JoinColumn(name = "senderId", unique = true) // Ensures one-to-one relationship
//    User user;
}
