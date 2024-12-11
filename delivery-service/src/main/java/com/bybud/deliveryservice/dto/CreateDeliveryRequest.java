package com.bybud.deliveryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateDeliveryRequest {

    @NotBlank(message = "Customer ID is required.")
    @Size(max = 50, message = "Customer ID cannot exceed 50 characters.")
    private String customerId;

    @NotBlank(message = "Delivery details are required.")
    @Size(max = 255, message = "Delivery details cannot exceed 255 characters.")
    private String deliveryDetails;

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
