package com.devteria.identityservice.dto.response;

import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodItemOrderResponse {
    String name;
    String description;
    BigDecimal price;
    int quantity;
}
