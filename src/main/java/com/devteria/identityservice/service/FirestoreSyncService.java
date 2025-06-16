package com.devteria.identityservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.devteria.identityservice.dto.response.EateryOrderResponse;
import com.devteria.identityservice.dto.response.OrderResponse;
import com.devteria.identityservice.dto.response.UserOrderResponse;
import com.devteria.identityservice.entity.Order;
import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.status.OrderStatus;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FirestoreSyncService {

    private static final String COLLECTION_NAME = "orders";

    // src/main/java/com/devteria/identityservice/service/FirestoreSyncService.java

    public void createOrderInFirestore(OrderResponse orderResponse) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            Map<String, Object> data = new HashMap<>();
            data.put("order_id", orderResponse.getOrderId());
            data.put("status", orderResponse.getOrderStatus());
            data.put("total_price", orderResponse.getTotalPrice());
            data.put("created_at", Timestamp.now());

            UserOrderResponse purchaser = orderResponse.getPurchaserResponse();
            if (purchaser != null) {
                data.put("purchaser_id", purchaser.getId());
                data.put("purchaser_name", purchaser.getUsername()); // Add this line
                data.put("purchaser_lat", purchaser.getPurchaserLat());
                data.put("purchaser_lon", purchaser.getPurchaserLon());
            }

            EateryOrderResponse eatery = orderResponse.getEateryOrderResponse();
            if (eatery != null) {
                data.put("eateryName", eatery.getName());
                data.put("eatery_contact_number", eatery.getContactNumber()); // Add this line
            }

            List<Map<String, Object>> foodItems = orderResponse.getFoodItemResponses().stream()
                    .map(item -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", item.getName());
                        map.put("quantity", item.getQuantity());
                        map.put("description", item.getDescription());
                        map.put("size", item.getSize());
                        return map;
                    })
                    .toList();
            data.put("foodItems", foodItems);

            db.collection(COLLECTION_NAME).document(orderResponse.getOrderId()).set(data);
            System.out.println("✅ Order created in Firestore: " + orderResponse.getOrderId());

        } catch (Exception e) {
            System.err.println("❌ Firestore create failed: " + e.getMessage());
        }
    }

    public void updateOrderInFirestore(Order order, Double deliveryManLat, Double deliveryManLon) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            Map<String, Object> updateFields = new HashMap<>();
            updateFields.put("delivery_man_id", getUserId(order.getDeliveryman()));
            updateFields.put("delivery_man_name", order.getDeliveryman().getUsername());
            updateFields.put("delivery_man_lat", deliveryManLat);
            updateFields.put("delivery_man_lon", deliveryManLon);
            updateFields.put("status", getStatus(order));
            updateFields.put("updated_at", Timestamp.now());

            db.collection(COLLECTION_NAME)
                    .document(order.getOrderId())
                    .set(updateFields, com.google.cloud.firestore.SetOptions.merge());

            System.out.println("✅ Order updated in Firestore: " + order.getOrderId());

        } catch (Exception e) {
            System.err.println("❌ Firestore update failed: " + e.getMessage());
        }
    }

    public void deleteOrderFromFirestore(String orderId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<WriteResult> writeResult =
                    db.collection(COLLECTION_NAME).document(orderId).delete();
            writeResult.get();
            log.info("✅ Order {} deleted from Firestore", orderId);
        } catch (Exception e) {
            log.error("❌ Failed to delete order from Firestore: {}", e.getMessage(), e);
        }
    }

    public void updateOrderStatusInFirestore(String orderId, OrderStatus status) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", status.name());
            updates.put("updated_at", Timestamp.now());

            ApiFuture<WriteResult> future =
                    db.collection(COLLECTION_NAME).document(orderId).update(updates);

            future.get();
            log.info("✅ Order status updated in Firestore: {}", orderId);
        } catch (Exception e) {
            log.error("❌ Failed to update order status in Firestore: {}", e.getMessage(), e);
        }
    }

    private String getUserId(User user) {
        return user != null ? user.getId() : null;
    }

    private String getStatus(Order order) {
        return order.getOrderStatus() != null ? order.getOrderStatus().name() : "UNKNOWN";
    }
}
