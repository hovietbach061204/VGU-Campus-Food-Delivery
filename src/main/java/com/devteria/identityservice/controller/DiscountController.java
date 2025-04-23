package com.devteria.identityservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.devteria.identityservice.dto.request.ApiResponse;
import com.devteria.identityservice.dto.request.DiscountRequest;
import com.devteria.identityservice.dto.response.DiscountResponse;
import com.devteria.identityservice.service.DiscountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;

    @PostMapping
    ApiResponse<DiscountResponse> createDiscount(@RequestBody DiscountRequest request) {
        return ApiResponse.<DiscountResponse>builder()
                .result(discountService.createDiscount(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<DiscountResponse>> getAllDiscounts() {
        return ApiResponse.<List<DiscountResponse>>builder()
                .result(discountService.getAllDiscounts())
                .build();
    }

    @GetMapping("/{discountId}")
    ApiResponse<DiscountResponse> getDiscount(@PathVariable String discountId) {
        return ApiResponse.<DiscountResponse>builder()
                .result(discountService.getDiscount(discountId))
                .build();
    }

    @DeleteMapping("/{discountId}")
    ApiResponse<Void> deleteDiscount(@PathVariable String discountId) {
        discountService.deleteDiscount(discountId);
        return ApiResponse.<Void>builder().build();
    }
}
