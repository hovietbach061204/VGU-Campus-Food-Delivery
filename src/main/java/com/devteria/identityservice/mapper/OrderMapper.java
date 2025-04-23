package com.devteria.identityservice.mapper;

import com.devteria.identityservice.dto.request.OrderRequest;
import com.devteria.identityservice.dto.response.OrderResponse;
import com.devteria.identityservice.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "foodItems", ignore = true)
    @Mapping(target = "discounts", ignore = true)
    @Mapping(target = "eatery", ignore = true)
    @Mapping(target = "purchaser", ignore = true)
    @Mapping(target = "deliveryman", ignore = true)
    Order toOrder(OrderRequest orderRequest);

    @Mapping(target = "purchaserResponse", source = "purchaser")
    @Mapping(target = "deliverymanResponse", source = "deliveryman")
    OrderResponse toOrderResponse(Order order);
}