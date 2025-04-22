package com.devteria.identityservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EateryMenuResponse {
    String name;
    String contactNumber;
    String location;
    Set<FoodItemMenuResponse> foodItemMenuResponses;
}
