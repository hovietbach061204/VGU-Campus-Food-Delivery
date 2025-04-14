package com.devteria.identityservice.entity;

import java.util.Set;

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
public class Eatery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String eateryId;

    String name;

    @Column(name = "location")
    String location;

    @Column(name = "contactNumber")
    String contactNumber;

    @ManyToMany
    Set<FoodItem> foodItems;
}
