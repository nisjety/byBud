package com.bybud.common.dto;

import com.bybud.common.model.DeliveryStatus;

import java.time.LocalDateTime;

public class DeliveryResponse {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long courierId;
    private String courierUsername;
    private String deliveryDetails;
    private String pickupAddress;
    private String deliveryAddress;
    private DeliveryStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Long getCourierId() { return courierId; }
    public void setCourierId(Long courierId) { this.courierId = courierId; }

    public String getCourierUsername() { return courierUsername; }
    public void setCourierUsername(String courierUsername) { this.courierUsername = courierUsername; }

    public String getDeliveryDetails() { return deliveryDetails; }
    public void setDeliveryDetails(String deliveryDetails) { this.deliveryDetails = deliveryDetails; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    public static DeliveryResponse fromDTO(DeliveryDTO dto, String customerName, String courierUsername) {
        DeliveryResponse response = new DeliveryResponse();
        response.setId(dto.getId());
        response.setCustomerId(dto.getCustomerId());
        response.setCustomerName(customerName);
        response.setCourierId(dto.getCourierId());
        response.setCourierUsername(courierUsername);
        response.setDeliveryDetails(dto.getDeliveryDetails());
        response.setPickupAddress(dto.getPickupAddress());
        response.setDeliveryAddress(dto.getDeliveryAddress());
        response.setStatus(dto.getStatus());
        response.setCreatedDate(dto.getCreatedDate());
        response.setUpdatedDate(dto.getUpdatedDate());
        return response;
    }
}
