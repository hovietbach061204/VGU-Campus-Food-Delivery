package com.devteria.identityservice.dto.response;

import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
