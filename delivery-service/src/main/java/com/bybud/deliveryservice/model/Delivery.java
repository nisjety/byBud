package com.bybud.deliveryservice.model;

import com.bybud.common.model.BaseEntity;
import com.bybud.common.model.DeliveryStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "deliveries")
public class Delivery extends BaseEntity {

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "courier_id")
    private Long courierId;

    @Column(name = "delivery_details", nullable = false)
    private String deliveryDetails;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatus status = DeliveryStatus.CREATED;

    public Delivery() {}

    public Delivery(Long customerId, String deliveryDetails, String pickupAddress, String deliveryAddress) {
        this.customerId = customerId;
        this.deliveryDetails = deliveryDetails;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.status = DeliveryStatus.CREATED;
    }

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getCourierId() { return courierId; }
    public void setCourierId(Long courierId) { this.courierId = courierId; }

    public String getDeliveryDetails() { return deliveryDetails; }
    public void setDeliveryDetails(String deliveryDetails) { this.deliveryDetails = deliveryDetails; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }
}

