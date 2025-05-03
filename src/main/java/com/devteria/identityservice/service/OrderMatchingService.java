package com.devteria.identityservice.service;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.devteria.identityservice.entity.Order;
import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.repository.OrderRepository;
import com.devteria.identityservice.repository.UserRepository;
import com.devteria.identityservice.status.OrderStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderMatchingService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final FirestoreSyncService firestoreSyncService;

    @Transactional
//    @PreAuthorize("hasRole('DELIVERY MAN')")
    public boolean acceptOrder(String deliverymanId, String orderId) {
        try {
            // Fetch the order with a pessimistic lock
            Optional<Order> optionalOrder = orderRepository.findByIdForUpdate(orderId);
            if (optionalOrder.isEmpty()) {
                return false; // Order not found
            }

            Order order = optionalOrder.get();

            // Check if the order is already assigned
            if (order.getDeliveryman() != null) {
                return false; // Order already assigned
            }

            // Fetch the deliveryman
            User deliveryman = userRepository.findById(deliverymanId)
                    .orElseThrow(() -> new RuntimeException("Deliveryman not found"));

            // Update the order's status and deliveryman
            order.setDeliveryman(deliveryman);
            order.setOrderStatus(OrderStatus.ASSIGNED);
            order.setUpdatedAt(LocalDate.now());

            // Save the updated order
            orderRepository.save(order);

            // Sync with Firestore (if applicable)
            firestoreSyncService.updateOrderInFirestore(order);

            return true; // Successfully accepted
        } catch (Exception e) {
            // Log the exception and return false
            System.err.println("Failed to accept order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
//    @PreAuthorize("hasRole('DELIVERY MAN')")
    public boolean updateOrderStatus(String deliverymanId, String orderId, OrderStatus newStatus) {
        try {
            // Fetch the order with a pessimistic lock
            Optional<Order> optionalOrder = orderRepository.findByIdForUpdate(orderId);
            if (optionalOrder.isEmpty()) {
                return false; // Order not found
            }

            Order order = optionalOrder.get();

            if(order.getDeliveryman() == null || !order.getDeliveryman().getId().equals(deliverymanId)){
                return false;
            }

            // Validate the status transition
            if ((newStatus == OrderStatus.PICKED_UP && order.getOrderStatus() != OrderStatus.ASSIGNED) ||
                    (newStatus == OrderStatus.DELIVERED && order.getOrderStatus() != OrderStatus.PICKED_UP)) {
                return false; // Invalid status transition
            }

            order.setOrderStatus(newStatus);
            order.setUpdatedAt(LocalDate.now());
            orderRepository.save(order);

            firestoreSyncService.updateOrderInFirestore(order);

            return true;

        }catch (Exception e) {
            // Log the exception and return false
            System.err.println("Failed to update order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

}
