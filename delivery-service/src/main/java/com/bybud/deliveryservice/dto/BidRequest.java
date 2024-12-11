package com.bybud.deliveryservice.dto;

import jakarta.validation.constraints.NotBlank;

public class BidRequest {

    @NotBlank(message = "Delivery ID is required.")
    private Long deliveryId;

    @NotBlank(message = "Courier ID is required.")
    private String courierId;

    private String note; // Optional note from the courier

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
