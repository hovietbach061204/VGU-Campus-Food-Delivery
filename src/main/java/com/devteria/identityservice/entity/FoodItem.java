package com.devteria.identityservice.entity;

import java.math.BigDecimal;

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
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String fooItemId;

    String name;
    String description;

    @Column(name = "price", precision = 10, scale = 2)
    BigDecimal price;

    @Column(name = "imageUrl")
    String imageUrl;

    int quantity;
}
