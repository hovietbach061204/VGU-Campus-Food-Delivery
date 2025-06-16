package com.devteria.identityservice.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devteria.identityservice.dto.request.FoodItemOrderRequest;
import com.devteria.identityservice.dto.request.OrderRequest;
import com.devteria.identityservice.dto.response.OrderResponse;
import com.devteria.identityservice.dto.shared.OrderedItem;
import com.devteria.identityservice.entity.Discount;
import com.devteria.identityservice.entity.Order;
import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.exception.AppException;
import com.devteria.identityservice.exception.ErrorCode;
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
    public OrderResponse createOrder(OrderRequest request) {
        try {
            if (request == null) {
                throw new IllegalArgumentException("OrderRequest must not be null");
            }
            if (request.getPurchaser().getPurchaserId() == null) {
                throw new IllegalArgumentException("Purchaser ID is required");
            }
            if (request.getFoodItems() == null || request.getFoodItems().isEmpty()) {
                throw new IllegalArgumentException("At least one food item is required");
            }

            var order = orderMapper.toOrder(request);
            order.setOrderStatus(OrderStatus.PENDING);

            var foodItemIds = request.getFoodItems().stream()
                    .map(FoodItemOrderRequest::getName)
                    .toList();

            var foodItems = foodItemRepository.findAllById(foodItemIds);

            Set<Discount> discounts = new HashSet<>();
            if (request.getVoucherCode() != null && !request.getVoucherCode().isEmpty()) {
                var foundDiscounts = discountRepository.findAllById(request.getVoucherCode());
                if (foundDiscounts.size() != request.getVoucherCode().size()) {
                    throw new RuntimeException("Some voucher codes are invalid");
                }
                discounts.addAll(foundDiscounts);
            }

            order.setDiscounts(discounts);
            // order.setFoodItems(new HashSet<>(foodItems));

            order.setCreatedAt(LocalDate.now());

            AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.ZERO);

            Map<String, OrderedItem> responseMap = new LinkedHashMap<>();

            for (var req : request.getFoodItems()) {
                var foodItem = foodItems.stream()
                        .filter(f -> f.getName().equals(req.getName()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Food item not found: " + req.getName()));

                String size = req.getSize() != null ? req.getSize().name() : "MEDIUM";
                String key = req.getName() + "|" + size;

                OrderedItem existing = responseMap.get(key);
                if (existing != null) {
                    existing.setQuantity(existing.getQuantity() + req.getQuantity());
                } else {
                    responseMap.put(
                            key,
                            OrderedItem.builder()
                                    .name(req.getName())
                                    .description(
                                            req.getDescription() != null
                                                            && !req.getDescription()
                                                                    .isBlank()
                                                    ? req.getDescription()
                                                    : foodItem.getDescription())
                                    .price(foodItem.getPrice())
                                    .quantity(req.getQuantity())
                                    .size(size)
                                    .build());
                }

                BigDecimal itemTotal = foodItem.getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
                totalPrice.set(totalPrice.get().add(itemTotal));
            }

            int totalDiscountPercentage =
                    discounts.stream().mapToInt(Discount::getDiscountPercentage).sum();
            BigDecimal discountMultiplier = BigDecimal.valueOf(1 - (totalDiscountPercentage / 100.0));
            totalPrice.set(totalPrice.get().multiply(discountMultiplier));

            order.setTotalPrice(totalPrice.get());

            // if (foodItems.size() != foodItemIds.size()) {
            // throw new RuntimeException("Some food items were not found");
            // }

            var eatery = eateryRepository
                    .findById(request.getEateryName())
                    .orElseThrow(() -> new RuntimeException("Eatery not found"));
            order.setEatery(eatery);

            var purchaser = userRepository
                    .findById(request.getPurchaser().getPurchaserId())
                    .orElseThrow(() -> new RuntimeException("Purchaser not found"));
            order.setPurchaser(purchaser);

            order = orderRepository.save(order);

            var response = orderMapper.toOrderResponse(order);
            response.setFoodItemResponses(new ArrayList<>(responseMap.values()));

            response.getPurchaserResponse()
                    .setPurchaserLat(request.getPurchaser().getPurchaserLat());
            response.getPurchaserResponse()
                    .setPurchaserLon(request.getPurchaser().getPurchaserLon());

            System.out.println("\n✅ Debug: Order saved to DB");
            System.out.println("  ➤ Order ID: " + order.getOrderId());
            System.out.println("  ➤ Total Price: " + order.getTotalPrice());
            System.out.println("  ➤ Discount Count: "
                    + (order.getDiscounts() != null ? order.getDiscounts().size() : 0));
            System.out.println("  ➤ Eatery: "
                    + (order.getEatery() != null ? order.getEatery().getName() : "N/A"));
            System.out.println("  ➤ Purchaser ID: "
                    + (order.getPurchaser() != null ? order.getPurchaser().getId() : "N/A"));
            System.out.println("  ➤ Created At: " + order.getCreatedAt());

            // Log each food item to be pushed to Firestore
            System.out.println("\n📦 Food Items for Firestore:");
            responseMap.forEach((key, item) -> {
                System.out.println("  ➤ Key: " + key);
                System.out.println("     Name: " + item.getName());
                System.out.println("     Size: " + item.getSize());
                System.out.println("     Quantity: " + item.getQuantity());
                System.out.println("     Description: " + item.getDescription());
                System.out.println("     Price per unit: " + item.getPrice());
            });

            System.out.println("\n🧭 Purchaser location:");
            System.out.println("  ➤ Latitude: " + request.getPurchaser().getPurchaserLat());
            System.out.println("  ➤ Longitude: " + request.getPurchaser().getPurchaserLon());

            firestoreSyncService.createOrderInFirestore(response);
            System.out.println("🔥 Firestore method called for: " + order.getOrderId());

            return response;
        } catch (Exception e) {
            log.error("❌ Order creation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Order creation failed", e);
        }
    }

    public List<OrderResponse> getPendingOrdersByPurchaser(String purchaserId) {
        return orderRepository.findByOrderStatusAndPurchaserId(OrderStatus.PENDING, purchaserId).stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    public OrderResponse getOrder(String orderId) {
        return orderRepository
                .findById(orderId)
                .map(orderMapper::toOrderResponse)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @Transactional
    public boolean acceptOrder(String driverId, String orderId, Double deliveryManLat, Double deliveryManLon) {
        try {
            Optional<Order> optionalOrder = orderRepository.findByIdForUpdate(orderId);
            if (optionalOrder.isEmpty()) {
                return false;
            }

            Order order = optionalOrder.get();

            // If already assigned, deny
            if (order.getDeliveryman() != null) {
                return false;
            }

            // Fetch the driver User entity
            User driver = userRepository.findById(driverId).orElseThrow(() -> new RuntimeException("Driver not found"));

            // Assign and update order
            order.setDeliveryman(driver);
            order.setOrderStatus(OrderStatus.ASSIGNED);
            order.setUpdatedAt(LocalDate.now());

            orderRepository.save(order);

            // Pass deliveryManLat and deliveryManLon to Firestore
            firestoreSyncService.updateOrderInFirestore(order, deliveryManLat, deliveryManLon);

            return true;

        } catch (Exception e) {
            System.err.println("Failed to accept order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public void cancelOrder(String orderId) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        // Update status instead of deleting
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Update in Firestore
        firestoreSyncService.updateOrderStatusInFirestore(orderId, OrderStatus.CANCELLED);

        log.info("Order {} successfully cancelled in both MySQL and Firestore", orderId);
    }

    @Transactional
    public void deleteOrder(String orderId) {
        // First check if the order exists
        var order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        // Delete from MySQL
        orderRepository.deleteById(orderId);

        // Delete from Firestore
        firestoreSyncService.deleteOrderFromFirestore(orderId);

        log.info("Order {} successfully deleted from both MySQL and Firestore", orderId);
    }

    public OrderResponse updateOrderStatus(String orderId) {
        System.out.println("[DEBUG] Incoming request to update order status for orderId: " + orderId);

        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            System.err.println("[ERROR] Order not found with ID: " + orderId);
            return new AppException(ErrorCode.ORDER_NOT_FOUND);
        });

        System.out.println("[DEBUG] Current order status: " + order.getOrderStatus());

        switch (order.getOrderStatus()) {
            case ASSIGNED:
                order.setOrderStatus(OrderStatus.DELIVERING);
                System.out.println("[DEBUG] Order status updated: ASSIGNED -> DELIVERING");
                break;
            case DELIVERING:
                order.setOrderStatus(OrderStatus.DELIVERED);
                System.out.println("[DEBUG] Order status updated: DELIVERING -> DELIVERED");
                break;
            default:
                System.err.println("[ERROR] Invalid transition from status: " + order.getOrderStatus());
                throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }

        orderRepository.save(order);
        System.out.println("[DEBUG] Order saved with new status: " + order.getOrderStatus());

        firestoreSyncService.updateOrderStatusInFirestore(orderId, order.getOrderStatus());
        System.out.println(
                "[DEBUG] Firestore synced for orderId: " + orderId + " with status: " + order.getOrderStatus());

        OrderResponse response = orderMapper.toOrderResponse(order);
        System.out.println("[DEBUG] Returning response: " + response);

        return response;
    }

    public OrderResponse revertOrderStatusToPending(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setOrderStatus(OrderStatus.PENDING);
        orderRepository.save(order);
        firestoreSyncService.updateOrderStatusInFirestore(orderId, OrderStatus.PENDING);
        return orderMapper.toOrderResponse(order);
    }
}
