package com.devteria.identityservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.devteria.identityservice.dto.request.OrderRequest;
import com.devteria.identityservice.dto.response.ApiResponse;
import com.devteria.identityservice.dto.response.OrderResponse;
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

    @PostMapping
    ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(request))
                .build();
    }

    @PostMapping("/{orderId}/accept")
    public ResponseEntity<?> acceptOrder(
            @PathVariable String orderId,
            @RequestParam String driverId,
            @RequestParam Double deliveryManLat,
            @RequestParam Double deliveryManLon) {
        boolean accepted = orderService.acceptOrder(driverId, orderId, deliveryManLat, deliveryManLon);
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
    ApiResponse<Map<String, String>> delete(@PathVariable String orderId) {
        orderService.deleteOrder(orderId);

        return ApiResponse.<Map<String, String>>builder()
                .result(Map.of("message", "Order " + orderId + " successfully cancelled"))
                .build();
    }

    @PostMapping("/{orderId}/cancel")
    ApiResponse<Map<String, String>> cancelOrder(@PathVariable String orderId) {
        orderService.cancelOrder(orderId);

        return ApiResponse.<Map<String, String>>builder()
                .result(Map.of("status", "CANCELLED", "message", "Order " + orderId + " successfully cancelled"))
                .build();
    }

    @PutMapping("/{orderId}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable String orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderStatus(orderId))
                .build();
    }

    @PutMapping("/{orderId}/status/pending")
    public ApiResponse<OrderResponse> revertOrderStatusToPending(@PathVariable String orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.revertOrderStatusToPending(orderId))
                .build();
    }
}
