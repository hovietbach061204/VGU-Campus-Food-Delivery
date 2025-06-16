package com.devteria.identityservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.devteria.identityservice.dto.request.FoodItemRequest;
import com.devteria.identityservice.dto.response.ApiResponse;
import com.devteria.identityservice.dto.response.FoodItemMenuResponse;
import com.devteria.identityservice.service.FoodItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/food-items")
@RequiredArgsConstructor
public class FoodItemController {
    private final FoodItemService foodItemService;

    @PostMapping
    ApiResponse<FoodItemMenuResponse> createFoodItem(@RequestBody FoodItemRequest request) {
        return ApiResponse.<FoodItemMenuResponse>builder()
                .result(foodItemService.createFoodItem(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<FoodItemMenuResponse>> getAllFoodItems() {
        return ApiResponse.<List<FoodItemMenuResponse>>builder()
                .result(foodItemService.getAllFoodItems())
                .build();
    }

    @GetMapping("/{foodItemId}")
    ApiResponse<FoodItemMenuResponse> getDiscount(@PathVariable String foodItemId) {
        return ApiResponse.<FoodItemMenuResponse>builder()
                .result(foodItemService.getFoodItem(foodItemId))
                .build();
    }

    @DeleteMapping("/{foodItemId}")
    ApiResponse<Void> deleteFoodItem(@PathVariable String foodItemId) {
        foodItemService.deleteFoodItem(foodItemId);
        return ApiResponse.<Void>builder().build();
    }
}
