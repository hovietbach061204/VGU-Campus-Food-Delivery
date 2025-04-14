package com.devteria.identityservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "order_booked")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String orderId;

    @GeneratedValue(strategy = GenerationType.UUID)
    String purchaserId;

    @GeneratedValue(strategy = GenerationType.UUID)
    String deliverymanId;

    @GeneratedValue(strategy = GenerationType.UUID)
    int statusId;

    @Column(name = "totalPrice", precision = 10, scale = 2)
    BigDecimal totalPrice;

    @GeneratedValue(strategy = GenerationType.UUID)
    int discountId;

    LocalDate createdAt;
    LocalDate updatedAt;

    @ManyToOne
    @JoinColumn(name = "userId")
    User user;

//    @OneToOne(mappedBy = "order")
//    ChatRoom chatRoom;

    @ManyToMany
    Set<Eatery> eateries;

}
