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
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String notificationId;

    @Column(name = "title")
    String title;

    String content;
    boolean isRead;
    LocalDate sentAt;

    @ManyToOne
    @JoinColumn(name = "userId")
    User user;
}
