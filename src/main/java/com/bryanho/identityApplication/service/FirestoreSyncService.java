package com.bryanho.identityApplication.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FirestoreSyncService {

    public void pushOrderToFirestore() {
        try {
            Firestore db = FirestoreClient.getFirestore();

            // Map<String, Object> data = new HashMap<>();
            // data.put("order_id", order.getOrderId());
            // data.put("purchaser_id", order.getPurchaserId());
            // data.put("delivery_man_id", order.getDeliveryManId()); // may be null
            // data.put("status_id", order.getStatusId());
            // data.put("total_price", order.getTotalPrice());
            // // data.put("created_at", Timestamp.now());

            // db.collection("orders")
            //   .document(order.getOrderId().toString())
            //   .set(data);

            System.out.println("✅ Order pushed to Firestore: ");
        } catch (Exception e) {
            System.err.println("❌ Failed to push order to Firestore: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
