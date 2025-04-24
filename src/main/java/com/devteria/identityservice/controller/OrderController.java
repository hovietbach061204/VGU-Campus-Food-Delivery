package com.devteria.identityservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.devteria.identityservice.dto.request.ApiResponse;
import com.devteria.identityservice.dto.request.OrderRequest;
import com.devteria.identityservice.dto.response.OrderResponse;
import com.devteria.identityservice.service.OrderMatchingService;
import com.devteria.identityservice.service.OrderService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderController {
    final OrderService orderService;
    final OrderMatchingService orderMatchingService;

    @PostMapping
    ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(request))
                .build();
    }

    @PostMapping("/{orderId}/accept")
    public ResponseEntity<?> acceptOrder(@PathVariable String orderId, @RequestParam String driverId) {
        boolean accepted = orderMatchingService.acceptOrder(driverId, orderId);
        if (accepted) {
            return ResponseEntity.ok(Map.of("status", "ACCEPTED"));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status", "ALREADY_ASSIGNED"));
        }
    }

    @GetMapping
    ApiResponse<List<OrderResponse>> getAllOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getAllOrders())
                .build();
    }

    @GetMapping("/{orderId}")
    ApiResponse<OrderResponse> getOrder(@PathVariable String orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrder(orderId))
                .build();
    }

    @GetMapping("/pending/{purchaserId}")
    ApiResponse<List<OrderResponse>> getPendingOrdersByPurchaser(@PathVariable String purchaserId) {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getPendingOrdersByPurchaser(purchaserId))
                .build();
    }

    @DeleteMapping("/{orderId}")
    ApiResponse<Void> delete(@PathVariable String orderId) {
        orderService.deleteOrder(orderId);
        return ApiResponse.<Void>builder().build();
    }
}
