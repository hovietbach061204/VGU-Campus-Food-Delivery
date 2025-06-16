package com.devteria.identityservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.devteria.identityservice.dto.shared.OrderedItem;
import com.devteria.identityservice.status.OrderStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String orderId;
    Set<DiscountOrderResponse> discountResponses;
    LocalDate createdAt;
    List<OrderedItem> foodItemResponses;
    EateryOrderResponse eateryOrderResponse;
    OrderStatus orderStatus;
    BigDecimal totalPrice;
    UserOrderResponse purchaserResponse; // New field for purchaser
    UserOrderResponse deliverymanResponse; // New field for deliveryman
}
