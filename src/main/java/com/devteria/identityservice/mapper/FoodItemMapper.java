package com.devteria.identityservice.mapper;

import com.devteria.identityservice.dto.request.FoodItemRequest;
import com.devteria.identityservice.dto.response.FoodItemMenuResponse;
import com.devteria.identityservice.dto.response.FoodItemOrderResponse;
import com.devteria.identityservice.entity.FoodItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FoodItemMapper {
    FoodItem toFoodItem(FoodItemRequest request);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
//    @Mapping(target = "quantity", source = "quantity") // Map quantity
    FoodItemOrderResponse toFoodItemOrderResponse(FoodItem foodItem);
    FoodItemMenuResponse toFoodItemMenuResponse(FoodItem foodItem);
}