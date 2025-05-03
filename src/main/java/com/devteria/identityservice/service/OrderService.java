package com.devteria.identityservice.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.devteria.identityservice.dto.request.DiscountRequest;
import com.devteria.identityservice.dto.response.FoodItemOrderResponse;
import com.devteria.identityservice.entity.Discount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devteria.identityservice.dto.request.FoodItemOrderRequest;
import com.devteria.identityservice.dto.request.OrderRequest;
import com.devteria.identityservice.dto.response.OrderResponse;
import com.devteria.identityservice.entity.FoodItem;
import com.devteria.identityservice.mapper.OrderMapper;
import com.devteria.identityservice.repository.*;
import com.devteria.identityservice.status.OrderStatus;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

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
    private final FirestoreSyncService firestoreSyncService;
    final UserRepository userRepository;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse createOrder(OrderRequest request) {
        try {
            var order = orderMapper.toOrder(request);
            order.setOrderStatus(OrderStatus.PENDING);

            var foodItemIds = request.getFoodItems().stream().map(FoodItemOrderRequest::getName).toList();
            var foodItems = foodItemRepository.findAllById(foodItemIds);
            var discounts = discountRepository.findAllById(request.getVoucherCode());
            order.setDiscounts(new HashSet<>(discounts));

            order.setFoodItems(new HashSet<>(foodItems));
            order.setCreatedAt(LocalDate.now());

            AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.ZERO);
            var foodItemResponses = foodItems.stream().map(foodItem -> {
                int quantity = request.getFoodItems().stream()
                        .filter(f -> f.getName().equals(foodItem.getName()))
                        .findFirst()
                        .map(FoodItemOrderRequest::getQuantity)
                        .orElse(0);

                totalPrice.set(totalPrice.get().add(foodItem.getPrice().multiply(BigDecimal.valueOf(quantity))));

                // Map FoodItem to FoodItemOrderResponse and set quantity dynamically
                return FoodItemOrderResponse.builder()
                        .name(foodItem.getName())
                        .description(foodItem.getDescription())
                        .price(foodItem.getPrice())
                        .quantity(quantity) // Set quantity dynamically
                        .build();
            }).toList();

            int totalDiscountPercentage = discounts.stream()
                    .mapToInt(Discount::getDiscountPercentage)
                    .sum();

            // Apply discount to total price
            BigDecimal discountMultiplier = BigDecimal.valueOf(1 - (totalDiscountPercentage / 100.0));
            totalPrice.set(totalPrice.get().multiply(discountMultiplier));

            order.setTotalPrice(totalPrice.get());

            if (foodItems.size() != foodItemIds.size()) {
                throw new RuntimeException("Some food items were not found");
            }

            var eatery = eateryRepository
                    .findById(request.getEateryName())
                    .orElseThrow(() -> new RuntimeException("Eatery not found"));
            order.setEatery(eatery);

            var purchaser = userRepository
                    .findById(request.getPurchaserId())
                    .orElseThrow(() -> new RuntimeException("Purchaser not found"));
            order.setPurchaser(purchaser);

            order = orderRepository.save(order);
            System.out.println("🔥 Firestore method called for: " + order.getOrderId());
            firestoreSyncService.createOrderInFirestore(order);

            // Build the response dynamically
            var response = orderMapper.toOrderResponse(order);
            response.setFoodItemResponses(new HashSet<>(foodItemResponses)); // Convert to Set
            return response;
        } catch (Exception e) {
            log.error("❌ Order creation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Order creation failed", e);
        }
    }

//    @Transactional
//    public List<OrderResponse> getPendingOrdersByPurchaser(String purchaserId) {
//        return orderRepository.findByOrderStatusAndPurchaserId(OrderStatus.PENDING, purchaserId)
//                .stream()
//                .map(orderMapper::toOrderResponse)
//                .toList();
//    }

    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse getOrder(String orderId) {
        return orderRepository
                .findById(orderId)
                .map(orderMapper::toOrderResponse)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @Transactional
    public List<OrderResponse> getsByPurchaserAndStatus(String purchaserId,OrderStatus status) {
        return orderRepository.findByOrderStatusAndPurchaserId(status, purchaserId)
                .stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(String orderId) {
        orderRepository.deleteById(orderId);
    }

}
