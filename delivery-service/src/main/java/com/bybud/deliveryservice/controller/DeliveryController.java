package com.bybud.deliveryservice.controller;

import com.bybud.deliveryservice.dto.*;
import com.bybud.deliveryservice.model.CourierBid;
import com.bybud.deliveryservice.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    // Create a new delivery
    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(@Valid @RequestBody CreateDeliveryRequest request) {
        DeliveryResponse response = deliveryService.createDelivery(request);
        return ResponseEntity.ok(response);
    }

    // Get all deliveries for a customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesForCustomer(@PathVariable String customerId) {
        List<DeliveryResponse> deliveries = deliveryService.getDeliveriesForCustomer(customerId);
        return ResponseEntity.ok(deliveries);
    }

    // Get all deliveries for a courier
    @GetMapping("/courier/{courierId}")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesForCourier(@PathVariable String courierId) {
        List<DeliveryResponse> deliveries = deliveryService.getDeliveriesForCourier(courierId);
        return ResponseEntity.ok(deliveries);
    }

    // Place a bid on a delivery
    @PostMapping("/bid")
    public ResponseEntity<String> placeBid(@Valid @RequestBody BidRequest request) {
        deliveryService.placeBid(request);
        return ResponseEntity.ok("Bid placed successfully");
    }

    // Get all bids for a delivery
    @GetMapping("/{deliveryId}/bids")
    public ResponseEntity<List<CourierBid>> getBidsForDelivery(@PathVariable Long deliveryId) {
        List<CourierBid> bids = deliveryService.getBidsForDelivery(deliveryId);
        return ResponseEntity.ok(bids);
    }

    // Update delivery status or assign a courier
    @PutMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponse> updateDelivery(@PathVariable Long deliveryId,
                                                           @Valid @RequestBody UpdateDeliveryRequest request) {
        DeliveryResponse response = deliveryService.updateDelivery(deliveryId, request);
        return ResponseEntity.ok(response);
    }
}
