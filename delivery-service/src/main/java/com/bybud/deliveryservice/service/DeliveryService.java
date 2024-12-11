package com.bybud.deliveryservice.service;

import com.bybud.deliveryservice.dto.*;
import com.bybud.deliveryservice.exception.DeliveryNotFoundException;
import com.bybud.deliveryservice.model.CourierBid;
import com.bybud.deliveryservice.model.Delivery;
import com.bybud.deliveryservice.repository.CourierBidRepository;
import com.bybud.deliveryservice.repository.DeliveryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final CourierBidRepository courierBidRepository;

    public DeliveryService(DeliveryRepository deliveryRepository, CourierBidRepository courierBidRepository) {
        this.deliveryRepository = deliveryRepository;
        this.courierBidRepository = courierBidRepository;
    }

    public DeliveryResponse createDelivery(CreateDeliveryRequest request) {
        Delivery delivery = new Delivery(request.getCustomerId(), request.getDeliveryDetails());
        Delivery savedDelivery = deliveryRepository.save(delivery);
        return mapToDeliveryResponse(savedDelivery);
    }

    public List<DeliveryResponse> getDeliveriesForCustomer(String customerId) {
        return deliveryRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToDeliveryResponse)
                .collect(Collectors.toList());
    }

    public List<DeliveryResponse> getDeliveriesForCourier(String courierId) {
        return deliveryRepository.findByCourierId(courierId)
                .stream()
                .map(this::mapToDeliveryResponse)
                .collect(Collectors.toList());
    }

    public void placeBid(BidRequest request) {
        CourierBid bid = new CourierBid(request.getDeliveryId(), request.getCourierId(), request.getNote());
        courierBidRepository.save(bid);
    }

    public List<CourierBid> getBidsForDelivery(Long deliveryId) {
        return courierBidRepository.findByDeliveryId(deliveryId);
    }

    public DeliveryResponse updateDelivery(Long deliveryId, UpdateDeliveryRequest request) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));

        if (request.getCourierId() != null) {
            delivery.setCourierId(request.getCourierId());
        }

        if (request.getStatus() != null) {
            delivery.setStatus(request.getStatus());
        }

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return mapToDeliveryResponse(updatedDelivery);
    }

    private DeliveryResponse mapToDeliveryResponse(Delivery delivery) {
        DeliveryResponse response = new DeliveryResponse();
        response.setId(delivery.getId());
        response.setCustomerId(delivery.getCustomerId());
        response.setCourierId(delivery.getCourierId());
        response.setDeliveryDetails(delivery.getDeliveryDetails());
        response.setStatus(delivery.getStatus());
        response.setCreatedDate(delivery.getCreatedDate());
        response.setUpdatedDate(delivery.getUpdatedDate());
        return response;
    }
}
