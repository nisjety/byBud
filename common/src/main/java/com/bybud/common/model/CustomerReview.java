package com.bybud.common.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customer_reviews")
public class CustomerReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String courierId;

    @Column(nullable = false)
    private int rating; // 1-5 stars

    @Column(length = 500)
    private String reviewText;

    @Column(nullable = false)
    private boolean verified; // Ensures reviews are tied to completed deliveries

    // Default Constructor
    public CustomerReview() {}

    // Parameterized Constructor
    public CustomerReview(String customerId, String courierId, int rating, String reviewText, boolean verified) {
        this.customerId = customerId;
        this.courierId = courierId;
        this.rating = rating;
        this.reviewText = reviewText;
        this.verified = verified;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
