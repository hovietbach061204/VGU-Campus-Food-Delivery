package com.devteria.identityservice.mapper;

import com.devteria.identityservice.dto.request.DiscountRequest;
import com.devteria.identityservice.dto.response.DiscountOrderResponse;
import com.devteria.identityservice.dto.response.DiscountResponse;
import com.devteria.identityservice.entity.Discount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountMapper {
    Discount toDiscount(DiscountRequest request);

    DiscountOrderResponse toDiscountOrderResponse(Discount discount);
    DiscountResponse toDiscountResponse(Discount discount);
}