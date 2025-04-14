package com.devteria.identityservice.entity;

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
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String chatId;
    String senderId;
    String recipientId;

//    @GeneratedValue(strategy = GenerationType.UUID)
//    String statusId;
//    @ManyToOne
//    @JoinColumn(name = "userId")
//    User user;
//
//    @OneToOne
//    @JoinColumn(name = "orderId", unique = true) // Ensures one-to-one relationship
//    Order order;
//    @OneToMany(mappedBy = "messages")
//    Set<Message> messages;
}
