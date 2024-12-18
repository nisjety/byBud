package com.bybud.deliveryservice.controller;

import com.bybud.common.dto.BaseResponse;
import com.bybud.common.dto.CreateDeliveryRequest;
import com.bybud.common.dto.DeliveryResponse;
import com.bybud.common.model.DeliveryStatus;
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

    @PostMapping
    public ResponseEntity<BaseResponse<DeliveryResponse>> createDelivery(@Valid @RequestBody CreateDeliveryRequest request) {
        DeliveryResponse response = deliveryService.createDelivery(request);
        return ResponseEntity.ok(BaseResponse.success("Delivery created successfully.", response));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<BaseResponse<List<DeliveryResponse>>> getDeliveriesForCustomer(@PathVariable("customerId") Long customerId) {
        List<DeliveryResponse> deliveries = deliveryService.getDeliveriesForCustomer(customerId);
        return ResponseEntity.ok(BaseResponse.success("Customer deliveries fetched successfully.", deliveries));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<DeliveryResponse>>> getAllDeliveries(@RequestParam Long userId) {
        List<DeliveryResponse> deliveries = deliveryService.getAllDeliveries(userId);
        return ResponseEntity.ok(BaseResponse.success("All deliveries fetched successfully.", deliveries));
    }

    @GetMapping("/courier/{courierId}")
    public ResponseEntity<BaseResponse<List<DeliveryResponse>>> getDeliveriesForCourier(@PathVariable("courierId") Long courierId) {
        List<DeliveryResponse> deliveries = deliveryService.getDeliveriesForCourier(courierId);
        return ResponseEntity.ok(BaseResponse.success("Courier deliveries fetched successfully.", deliveries));
    }

    @PutMapping("/{deliveryId}/accept/{courierId}")
    public ResponseEntity<BaseResponse<DeliveryResponse>> acceptDelivery(
            @PathVariable("deliveryId") Long deliveryId,
            @PathVariable("courierId") Long courierId
    ) {
        DeliveryResponse response = deliveryService.acceptDelivery(deliveryId, courierId);
        return ResponseEntity.ok(BaseResponse.success("Delivery accepted successfully by courier.", response));
    }


    @PutMapping("/{deliveryId}/status")
    public ResponseEntity<BaseResponse<DeliveryResponse>> updateDeliveryStatus(
            @PathVariable("deliveryId") Long deliveryId,
            @RequestParam("status") DeliveryStatus status,
            @RequestParam("userId") Long userId
    ) {
        DeliveryResponse response = deliveryService.updateDeliveryStatus(deliveryId, status, userId);
        return ResponseEntity.ok(BaseResponse.success("Delivery status updated successfully.", response));
    }
}
