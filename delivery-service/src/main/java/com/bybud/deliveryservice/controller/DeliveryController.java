package com.bybud.deliveryservice.controller;

import com.bybud.common.dto.BaseResponse;
import com.bybud.common.model.DeliveryStatus;
import com.bybud.deliveryservice.dto.CreateDeliveryRequest;
import com.bybud.deliveryservice.dto.DeliveryResponse;
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
    public ResponseEntity<BaseResponse<DeliveryResponse>> createDelivery(@Valid @RequestBody CreateDeliveryRequest request,
                                                                         @RequestHeader("Authorization") String authHeader) {
        String jwtToken = extractJwtFromHeader(authHeader);
        DeliveryResponse response = deliveryService.createDelivery(request, jwtToken);
        return ResponseEntity.ok(BaseResponse.success("Delivery created successfully.", response));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<DeliveryResponse>>> getDeliveries(@RequestHeader("Authorization") String authHeader) {
        String jwtToken = extractJwtFromHeader(authHeader);
        List<DeliveryResponse> deliveries = deliveryService.getDeliveries(jwtToken);
        return ResponseEntity.ok(BaseResponse.success("Deliveries fetched successfully.", deliveries));
    }

    @PutMapping("/{deliveryId}/accept")
    public ResponseEntity<BaseResponse<DeliveryResponse>> acceptDelivery(@PathVariable Long deliveryId,
                                                                         @RequestHeader("Authorization") String authHeader) {
        String jwtToken = extractJwtFromHeader(authHeader);
        DeliveryResponse response = deliveryService.acceptDelivery(deliveryId, jwtToken);
        return ResponseEntity.ok(BaseResponse.success("Delivery accepted successfully.", response));
    }

    @PutMapping("/{deliveryId}/status")
    public ResponseEntity<BaseResponse<DeliveryResponse>> updateDeliveryStatus(@PathVariable Long deliveryId,
                                                                               @RequestParam DeliveryStatus status,
                                                                               @RequestHeader("Authorization") String authHeader) {
        String jwtToken = extractJwtFromHeader(authHeader);
        DeliveryResponse response = deliveryService.updateDeliveryStatus(deliveryId, status, jwtToken);
        return ResponseEntity.ok(BaseResponse.success("Delivery status updated successfully.", response));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<BaseResponse<List<DeliveryResponse>>> getAllDeliveriesForAdmin(@RequestHeader("Authorization") String authHeader) {
        String jwtToken = extractJwtFromHeader(authHeader);
        List<DeliveryResponse> deliveries = deliveryService.getAllDeliveriesForAdmin(jwtToken);
        return ResponseEntity.ok(BaseResponse.success("All deliveries fetched successfully for admin.", deliveries));
    }

    // Helper to extract JWT from Authorization header
    private String extractJwtFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Authorization header is invalid or missing.");
    }
}
