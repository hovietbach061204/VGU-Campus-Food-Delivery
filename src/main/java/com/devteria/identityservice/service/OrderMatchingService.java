package com.devteria.identityservice.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.devteria.identityservice.entity.Order;
import com.devteria.identityservice.repository.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderMatchingService {
    private final OrderRepository orderRepository;
    private final FirestoreSyncService firestoreSyncService;

    @Transactional
    public boolean acceptOrder(Integer driverId, Integer orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty())
            return false;

        Order order = optionalOrder.get();
        if (order.getDeliveryManId() != null) {
            return false; // Already assigned
        }

        order.setDeliveryManId(driverId);
        order.setStatusId(2); // e.g., 2 = ASSIGNED
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        firestoreSyncService.pushOrderToFirestore(order);
        return true;
    }
}
