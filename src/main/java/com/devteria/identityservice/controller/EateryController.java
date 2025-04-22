package com.devteria.identityservice.controller;

import com.devteria.identityservice.dto.request.ApiResponse;
import com.devteria.identityservice.dto.request.EateryRequest;
import com.devteria.identityservice.dto.response.DiscountResponse;
import com.devteria.identityservice.dto.response.EateryMenuResponse;
import com.devteria.identityservice.dto.response.EateryOrderResponse;
import com.devteria.identityservice.service.EateryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eateries")
@RequiredArgsConstructor
public class EateryController {
    private final EateryService eateryService;

    @PostMapping
    ApiResponse<EateryMenuResponse> createEatery(@RequestBody EateryRequest request) {
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
    ApiResponse<EateryMenuResponse>  getEatery(@PathVariable String eateryName) {
        return ApiResponse.<EateryMenuResponse>builder()
                .result(eateryService.getEatery(eateryName))
                .build();
    }


    @DeleteMapping("/{eateryName}")
    ApiResponse<Void> deleteEatery(@PathVariable String eateryName) {
        eateryService.deleteEatery(eateryName);
        return ApiResponse.<Void>builder().build();
    }
}