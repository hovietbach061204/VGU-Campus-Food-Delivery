package com.devteria.identityservice.service;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.transaction.Transactional;

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
    public boolean acceptOrder(String driverId, String orderId) {
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
            firestoreSyncService.updateOrderInFirestore(order);

            return true;

        } catch (Exception e) {
            // Log the exception and return false to indicate failure
            System.err.println("Failed to accept order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
