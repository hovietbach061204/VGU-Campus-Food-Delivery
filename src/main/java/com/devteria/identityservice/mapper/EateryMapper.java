package com.devteria.identityservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.devteria.identityservice.dto.request.EaterUpdateRequest;
import com.devteria.identityservice.dto.request.EateryRequest;
import com.devteria.identityservice.dto.response.EateryMenuResponse;
import com.devteria.identityservice.dto.response.EateryOrderResponse;
import com.devteria.identityservice.entity.Eatery;

@Mapper(
        componentModel = "spring",
        uses = {FoodItemMapper.class})
public interface EateryMapper {
    @Mapping(target = "foodItems", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Eatery toEatery(EateryRequest eateryRequest);

    EateryOrderResponse toEateryOrderResponse(Eatery eatery);

    @Mapping(target = "foodItemMenuResponses", source = "foodItems")
    EateryMenuResponse toEateryMenuResponse(Eatery eatery);

    void updateEatery(@MappingTarget Eatery eatery, EaterUpdateRequest request);
}
