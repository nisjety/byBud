package com.bybud.deliveryservice.exception;

public class DeliveryNotFoundException extends RuntimeException {

    public DeliveryNotFoundException(String message) {
        super(message);
    }
}
