package com.devteria.identityservice.status;

public enum OrderStatus {
    PENDING, // Order created, awaiting driver
    ASSIGNED, // Driver accepted the order
    PICKED_UP, // Driver picked up the order from the eatery
    DELIVERING, // Driver is on the way to customer
    DELIVERED // Order handed to customer
}
