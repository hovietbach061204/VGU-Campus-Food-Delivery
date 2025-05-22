package com.devteria.identityservice.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.devteria.identityservice.dto.request.*;
import com.devteria.identityservice.dto.response.EateryMenuResponse;
import com.devteria.identityservice.service.EateryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/eateries")
@RequiredArgsConstructor
public class EateryController {
    private final EateryService eateryService;

    @PostMapping
    ApiResponse<EateryMenuResponse> createEatery(@RequestBody @Valid EateryRequest request) {
        return ApiResponse.<EateryMenuResponse>builder()
                .result(eateryService.createEatery(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<EateryMenuResponse>> getAllEateries() {
        return ApiResponse.<List<EateryMenuResponse>>builder()
                .result(eateryService.getAllEateries())
                .build();
    }

    @GetMapping("/{eateryName}")
    ApiResponse<EateryMenuResponse> getEatery(@PathVariable String eateryName) {
        return ApiResponse.<EateryMenuResponse>builder()
                .result(eateryService.getEatery(eateryName))
                .build();
    }

    @PutMapping("/food-items/{eateryName}")
    ApiResponse<EateryMenuResponse> addFoodItemToEatery(
            @PathVariable String eateryName, @RequestBody FoodItemRequest request) {
        return ApiResponse.<EateryMenuResponse>builder()
                .result(eateryService.addFoodItemToEatery(eateryName, request))
                .build();
    }

    @PutMapping("/{eateryName}")
    ApiResponse<EateryMenuResponse> updateEatery(
            @PathVariable String eateryName, @RequestBody EaterUpdateRequest request) {
        return ApiResponse.<EateryMenuResponse>builder()
                .result(eateryService.updateEatery(eateryName, request))
                .build();
    }

    @DeleteMapping("/{eateryName}")
    ApiResponse<Void> deleteEatery(@PathVariable String eateryName) {
        eateryService.deleteEatery(eateryName);
        return ApiResponse.<Void>builder().build();
    }

    @DeleteMapping("/{eateryName}/{foodName}")
    ApiResponse<Void> deleteFoodItemsFromEatery(@PathVariable String eateryName, @PathVariable String foodName) {
        eateryService.deleteFoodItemsFromEatery(eateryName, foodName);
        return ApiResponse.<Void>builder().build();
    }
}
