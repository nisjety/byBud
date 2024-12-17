package com.bybud.common.model;

//Represents the current status of a delivery.

public enum DeliveryStatus {
    CREATED,
    ASSIGNED,      // Courier assigned to the customer
    IN_PROGRESS,   // Courier is picking up or delivering the item
    COMPLETED,     // Delivery completed
    CANCELLED      // Delivery canceled
}
