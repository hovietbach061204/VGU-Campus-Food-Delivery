package com.devteria.identityservice.mapper;

import org.mapstruct.Mapper;

import com.devteria.identityservice.dto.request.DiscountRequest;
import com.devteria.identityservice.dto.response.DiscountOrderResponse;
import com.devteria.identityservice.dto.response.DiscountResponse;
import com.devteria.identityservice.entity.Discount;

@Mapper(componentModel = "spring")
public interface DiscountMapper {
    Discount toDiscount(DiscountRequest request);

    DiscountOrderResponse toDiscountOrderResponse(Discount discount);

    DiscountResponse toDiscountResponse(Discount discount);
}
