package com.devteria.identityservice.dto.response;

import com.devteria.identityservice.status.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String orderId;
    Set<DiscountOrderResponse> discountResponses;
    LocalDate createdAt;
    Set<FoodItemOrderResponse> foodItemResponses;
    EateryOrderResponse eateryOrderResponse;
    OrderStatus orderStatus;
    BigDecimal totalPrice;
    UserOrderResponse purchaserResponse; // New field for purchaser
    UserOrderResponse deliverymanResponse; // New field for deliveryman
}
