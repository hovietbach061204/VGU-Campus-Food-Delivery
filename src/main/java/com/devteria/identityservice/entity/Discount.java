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
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String discountId;

    @Column(name = "voucherCode", length = 255)
    String voucherCode;

    int discountPercentage;
    LocalDate expiryDate;
}
