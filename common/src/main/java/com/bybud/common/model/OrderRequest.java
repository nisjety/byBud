package com.bybud.common.model;

public class OrderRequest {

    private String customerId;
    private String deliveryDetails;

    // Default Constructor
    public OrderRequest() {}

    // Parameterized Constructor
    public OrderRequest(String customerId, String deliveryDetails) {
        this.customerId = customerId;
        this.deliveryDetails = deliveryDetails;
    }

    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDeliveryDetails() {
        return deliveryDetails;
    }

    public void setDeliveryDetails(String deliveryDetails) {
        this.deliveryDetails = deliveryDetails;
    }
}
