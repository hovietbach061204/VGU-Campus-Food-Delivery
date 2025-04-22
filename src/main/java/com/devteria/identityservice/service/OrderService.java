package com.devteria.identityservice.service;

import com.devteria.identityservice.dto.request.OrderRequest;
import com.devteria.identityservice.dto.response.OrderResponse;
import com.devteria.identityservice.entity.Eatery;
import com.devteria.identityservice.entity.Order;
import com.devteria.identityservice.mapper.OrderMapper;
import com.devteria.identityservice.repository.DiscountRepository;
import com.devteria.identityservice.repository.EateryRepository;
import com.devteria.identityservice.repository.FoodItemRepository;
import com.devteria.identityservice.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    final OrderRepository orderRepository;
    final OrderMapper orderMapper;
    final FoodItemRepository foodItemRepository;
    final DiscountRepository discountRepository;
    final EateryRepository eateryRepository;

    public OrderResponse createOrder(OrderRequest request) {
        var order = orderMapper.toOrder(request);

        var foodItems = foodItemRepository.findAllById(request.getFoodItems());
        order.setFoodItems(new HashSet<>(foodItems));

        var discounts = discountRepository.findAllById(request.getVoucherCode());
        order.setDiscounts(new HashSet<>(discounts));

        var eatery = eateryRepository.findById(request.getEateryName())
                .orElseThrow(() -> new RuntimeException("Eatery not found"));
        order.setEatery(eatery);

        order = orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toOrderResponse)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    public void deleteOrder(String orderId) {
        orderRepository.deleteById(orderId);
    }
}