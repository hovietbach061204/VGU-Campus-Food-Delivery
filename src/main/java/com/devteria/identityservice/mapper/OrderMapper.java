package com.devteria.identityservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.devteria.identityservice.dto.request.OrderRequest;
import com.devteria.identityservice.dto.response.OrderResponse;
import com.devteria.identityservice.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "foodItems", ignore = true)
    @Mapping(target = "discounts", ignore = true)
    @Mapping(target = "eatery", ignore = true)
    Order toOrder(OrderRequest orderRequest);

    OrderResponse toOrderResponse(Order order);
}
