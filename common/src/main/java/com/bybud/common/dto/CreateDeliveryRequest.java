package com.bybud.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateDeliveryRequest {

    @NotNull(message = "Customer ID is required.")
    private Long customerId;

    @NotBlank(message = "Delivery details are required.")
    @Size(max = 255, message = "Delivery details cannot exceed 255 characters.")
    private String deliveryDetails;

    @NotBlank(message = "Delivery address is required.")
    @Size(max = 255, message = "Delivery address cannot exceed 255 characters.")
    private String deliveryAddress;

    @NotBlank(message = "Pickup address is required.")
    @Size(max = 255, message = "Pickup address cannot exceed 255 characters.")
    private String pickupAddress;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getDeliveryDetails() { return deliveryDetails; }
    public void setDeliveryDetails(String deliveryDetails) { this.deliveryDetails = deliveryDetails; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
}
