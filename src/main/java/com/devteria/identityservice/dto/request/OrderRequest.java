package com.devteria.identityservice.dto.request;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequest {
    Set<String> voucherCode;
    String eateryName;
    Set<FoodItemOrderRequest> foodItems;
    String purchaserId;
}
