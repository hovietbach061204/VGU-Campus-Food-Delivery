package com.devteria.identityservice.mapper;

import com.devteria.identityservice.dto.request.FoodItemRequest;
import com.devteria.identityservice.dto.response.FoodItemMenuResponse;
import com.devteria.identityservice.dto.response.FoodItemOrderResponse;
import com.devteria.identityservice.entity.FoodItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FoodItemMapper {
    FoodItem toFoodItem(FoodItemRequest request);

    FoodItemOrderResponse toFoodItemOrderResponse(FoodItem foodItem);
    FoodItemMenuResponse toFoodItemMenuResponse(FoodItem foodItem);
}