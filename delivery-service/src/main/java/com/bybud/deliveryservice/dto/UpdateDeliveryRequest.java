package com.bybud.deliveryservice.dto;

import com.bybud.common.model.DeliveryStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateDeliveryRequest {

    @NotNull(message = "Delivery ID is required.")
    private Long deliveryId;

    private String courierId; // Optional update to assign a courier

    @NotNull(message = "Delivery status is required.")
    private DeliveryStatus status;

    // Getters and Setters
    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getCourierId() {
        return courierId;
    }

    public void setCourierId(String courierId) {
        this.courierId = courierId;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }
}
