package com.bryanho.identityApplication.entity;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity // indicates that this class is a table -> creates the table User in database
public class User {
    @Id // annotation for defining id -> indicates that id is the primary key
    @GeneratedValue(strategy = GenerationType.UUID) // makes the id will never be duplicated
    // and randomly generated
    String id;

    String username;
    String password;
    String firstname;
    String lastname;
    LocalDate dob;

    @ManyToMany
    Set<Role> roles; // in a set, the element is unique
}
