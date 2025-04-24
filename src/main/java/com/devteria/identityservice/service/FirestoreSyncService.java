package com.devteria.identityservice.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.devteria.identityservice.entity.Order;
import com.devteria.identityservice.entity.User;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class FirestoreSyncService {

    private static final String COLLECTION_NAME = "orders";

    public void createOrderInFirestore(Order order) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            System.out.println("📦 Order ID before Firestore push: " + order.getOrderId());

            Map<String, Object> data = new HashMap<>();
            data.put("order_id", order.getOrderId());
            data.put("purchaser_id", getUserId(order.getPurchaser()));
            data.put("delivery_man_id", getUserId(order.getDeliveryman()));
            data.put("status", getStatus(order));
            data.put("total_price", order.getTotalPrice());
            data.put("created_at", Timestamp.now());

            db.collection(COLLECTION_NAME).document(order.getOrderId()).set(data);
            System.out.println("✅ Order created in Firestore: " + order.getOrderId());

        } catch (Exception e) {
            System.err.println("❌ Firestore create failed: " + e.getMessage());
        }
    }

    public void updateOrderInFirestore(Order order) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            Map<String, Object> updateFields = new HashMap<>();
            updateFields.put("delivery_man_id", getUserId(order.getDeliveryman()));
            updateFields.put("status", getStatus(order));
            updateFields.put("updated_at", Timestamp.now());

            db.collection(COLLECTION_NAME).document(order.getOrderId()).update(updateFields);
            System.out.println("✅ Order updated in Firestore: " + order.getOrderId());

        } catch (Exception e) {
            System.err.println("❌ Firestore update failed: " + e.getMessage());
        }
    }

    private String getUserId(User user) {
        return user != null ? user.getId() : null;
    }

    private String getStatus(Order order) {
        return order.getOrderStatus() != null ? order.getOrderStatus().name() : "UNKNOWN";
    }
}
