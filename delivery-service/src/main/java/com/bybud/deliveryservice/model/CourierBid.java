package com.bybud.deliveryservice.model;

import com.bybud.common.model.BidStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "courier_bids")
public class CourierBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long deliveryId; // Associated delivery

    @Column(nullable = false)
    private String courierId; // ID of the courier placing the bid

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidStatus status; // Status of the bid (PENDING, ACCEPTED, REJECTED)

    @Column(nullable = false)
    private LocalDateTime bidDate; // Timestamp when the bid was placed

    @Column
    private String note; // Optional note from the courier

    // Default Constructor
    public CourierBid() {}

    // Parameterized Constructor
    public CourierBid(Long deliveryId, String courierId, String note) {
        this.deliveryId = deliveryId;
        this.courierId = courierId;
        this.note = note;
        this.status = BidStatus.PENDING;
        this.bidDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public BidStatus getStatus() {
        return status;
    }

    public void setStatus(BidStatus status) {
        this.status = status;
    }

    public LocalDateTime getBidDate() {
        return bidDate;
    }

    public void setBidDate(LocalDateTime bidDate) {
        this.bidDate = bidDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
