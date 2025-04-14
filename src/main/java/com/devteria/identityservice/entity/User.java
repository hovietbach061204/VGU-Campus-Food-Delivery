package com.devteria.identityservice.entity;

import java.time.LocalDate;
import java.util.Set;

import com.devteria.identityservice.status.Status;
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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;

    String password;
    String firstName;
    LocalDate dob;
    String lastName;
    private Status status;

    @ManyToMany
    Set<Role> roles;

    @OneToMany(mappedBy = "user")
    Set<Order> orders;

//    @OneToMany(mappedBy = "user")
//    Set<Notification> notifications;


//    @OneToMany(mappedBy = "user")
//    Set<ChatRoom> chatboxes;
//    @OneToOne(mappedBy = "user")
//    Message message;
}
