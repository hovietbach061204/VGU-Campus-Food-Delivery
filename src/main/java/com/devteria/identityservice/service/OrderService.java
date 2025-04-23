package com.devteria.identityservice.service;

import com.devteria.identityservice.constant.PredefinedRole;
import com.devteria.identityservice.dto.request.FoodItemOrderRequest;
import com.devteria.identityservice.dto.request.OrderRequest;
import com.devteria.identityservice.dto.response.OrderResponse;
import com.devteria.identityservice.entity.Eatery;
import com.devteria.identityservice.entity.FoodItem;
import com.devteria.identityservice.entity.Order;
import com.devteria.identityservice.entity.Role;
import com.devteria.identityservice.mapper.OrderMapper;
import com.devteria.identityservice.repository.*;
import com.devteria.identityservice.status.OrderStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    final UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        var order = orderMapper.toOrder(request);
        order.setOrderStatus(OrderStatus.PENDING);
        var foodItemIds = request.getFoodItems().stream().map(FoodItemOrderRequest::getName).toList();

        var foodItems = foodItemRepository.findAllById(foodItemIds);
        order.setFoodItems(new HashSet<>(foodItems));

        order.setCreatedAt(LocalDate.now());

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (FoodItem foodItem : foodItems) {
            int quantity = request.getFoodItems().stream()
                    .filter(f -> f.getName().equals(foodItem.getName()))
                    .findFirst()
                    .map(FoodItemOrderRequest::getQuantity)
                    .orElse(0);
            totalPrice = totalPrice.add(foodItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }
        order.setTotalPrice(totalPrice);

        if (foodItems.size() != foodItemIds.size()) {
            throw new RuntimeException("Some food items were not found");
        }

        var discounts = discountRepository.findAllById(request.getVoucherCode());
        order.setDiscounts(new HashSet<>(discounts));

        var eatery = eateryRepository.findById(request.getEateryName())
                .orElseThrow(() -> new RuntimeException("Eatery not found"));
        order.setEatery(eatery);

        var purchaser = userRepository.findById(request.getPurchaserId())
                .orElseThrow(() -> new RuntimeException("Purchaser not found" + request.getDeliverymanId()));
        order.setPurchaser(purchaser);


        if (request.getDeliverymanId() != null) {
            var deliveryman = userRepository.findById(request.getDeliverymanId())
                    .orElseThrow(() -> new RuntimeException("Deliveryman not found"));
            order.setDeliveryman(deliveryman);
        }

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