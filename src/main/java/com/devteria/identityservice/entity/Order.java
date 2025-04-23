package com.devteria.identityservice.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.*;

import com.devteria.identityservice.status.OrderStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

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

    //    @GeneratedValue(strategy = GenerationType.UUID)
    //    String purchaserId;
    //
    //    @GeneratedValue(strategy = GenerationType.UUID)
    //    String deliverymanId;

    OrderStatus orderStatus;

    @Column(name = "totalPrice", precision = 10, scale = 2)
    BigDecimal totalPrice;

    LocalDate createdAt;
    LocalDate updatedAt;

    //    @ManyToOne
    //    @JoinColumn(name = "userId")
    //    User user;

    //    @OneToOne(mappedBy = "order")
    //    ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "eatery_name", referencedColumnName = "name", nullable = false)
    Eatery eatery;

    @ManyToMany
    Set<FoodItem> foodItems;

    @ManyToMany
    Set<Discount> discounts;
}
