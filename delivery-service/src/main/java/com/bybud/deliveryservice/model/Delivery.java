package com.bybud.deliveryservice.model;

import com.bybud.common.model.DeliveryStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerId; // Customer who created the delivery request

    @Column
    private String courierId; // Courier assigned to the delivery

    @Column(nullable = false)
    private String deliveryDetails; // Description of the delivery

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status; // Current status of the delivery

    @Column(nullable = false)
    private LocalDateTime createdDate; // When the delivery request was created

    @Column
    private LocalDateTime updatedDate; // Last updated timestamp

    // Default Constructor
    public Delivery() {}

    // Parameterized Constructor
    public Delivery(String customerId, String deliveryDetails) {
        this.customerId = customerId;
        this.deliveryDetails = deliveryDetails;
        this.status = DeliveryStatus.PENDING;
        this.createdDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCourierId() {
        return courierId;
    }

    public void setCourierId(String courierId) {
        this.courierId = courierId;
    }

    public String getDeliveryDetails() {
        return deliveryDetails;
    }

    public void setDeliveryDetails(String deliveryDetails) {
        this.deliveryDetails = deliveryDetails;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
        this.updatedDate = LocalDateTime.now();
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
