package com.devteria.identityservice.repository;

import java.util.List;
import java.util.Optional;

import com.devteria.identityservice.status.OrderStatus;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devteria.identityservice.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId")
    Optional<Order> findByIdForUpdate(@Param("orderId") String orderId);
    @Query("SELECT o FROM Order o WHERE o.orderStatus = :status AND o.purchaser.id = :purchaserId")
    List<Order> findByOrderStatusAndPurchaserId(@Param("status") OrderStatus status, @Param("purchaserId") String purchaserId);
}
