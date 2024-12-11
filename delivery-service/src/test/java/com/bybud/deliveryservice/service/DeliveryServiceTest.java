package com.bybud.deliveryservice.service;

import com.bybud.common.model.DeliveryStatus;
import com.bybud.deliveryservice.dto.*;
import com.bybud.deliveryservice.exception.DeliveryNotFoundException;
import com.bybud.deliveryservice.model.CourierBid;
import com.bybud.deliveryservice.model.Delivery;
import com.bybud.deliveryservice.repository.CourierBidRepository;
import com.bybud.deliveryservice.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeliveryServiceTest {

    private DeliveryService deliveryService;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private CourierBidRepository courierBidRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        deliveryService = new DeliveryService(deliveryRepository, courierBidRepository);
    }

    @Test
    void testCreateDelivery_Success() {
        CreateDeliveryRequest request = new CreateDeliveryRequest();
        request.setCustomerId("customer123");
        request.setDeliveryDetails("Package description");

        Delivery savedDelivery = new Delivery("customer123", "Package description");
        savedDelivery.setId(1L);
        savedDelivery.setStatus(DeliveryStatus.CREATED);
        savedDelivery.setCreatedDate(LocalDateTime.now());

        when(deliveryRepository.save(any(Delivery.class))).thenReturn(savedDelivery);

        DeliveryResponse result = deliveryService.createDelivery(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("customer123", result.getCustomerId());
        assertEquals("Package description", result.getDeliveryDetails());
        assertEquals(DeliveryStatus.CREATED, result.getStatus());
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void testGetDeliveriesForCustomer_Success() {
        Delivery delivery1 = new Delivery("customer123", "Package 1");
        Delivery delivery2 = new Delivery("customer123", "Package 2");
        delivery1.setId(1L);
        delivery2.setId(2L);

        when(deliveryRepository.findByCustomerId("customer123")).thenReturn(List.of(delivery1, delivery2));

        List<DeliveryResponse> result = deliveryService.getDeliveriesForCustomer("customer123");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Package 1", result.get(0).getDeliveryDetails());
        assertEquals("Package 2", result.get(1).getDeliveryDetails());
        verify(deliveryRepository).findByCustomerId("customer123");
    }

    @Test
    void testGetDeliveriesForCourier_Success() {
        Delivery delivery1 = new Delivery("customer123", "Package 1");
        delivery1.setCourierId("courier456");
        delivery1.setId(1L);

        when(deliveryRepository.findByCourierId("courier456")).thenReturn(List.of(delivery1));

        List<DeliveryResponse> result = deliveryService.getDeliveriesForCourier("courier456");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("courier456", result.getFirst().getCourierId());
        verify(deliveryRepository).findByCourierId("courier456");
    }

    @Test
    void testPlaceBid_Success() {
        BidRequest request = new BidRequest();
        request.setCourierId("courier456");
        request.setDeliveryId(1L);
        request.setNote("I can deliver this quickly.");

        CourierBid savedBid = new CourierBid(1L, "courier456", "I can deliver this quickly.");

        when(courierBidRepository.save(any(CourierBid.class))).thenReturn(savedBid);

        deliveryService.placeBid(request);

        verify(courierBidRepository).save(any(CourierBid.class));
    }

    @Test
    void testUpdateDelivery_Success() {
        Long deliveryId = 1L;
        Delivery existingDelivery = new Delivery("customer123", "Package 1");
        existingDelivery.setId(deliveryId);
        existingDelivery.setStatus(DeliveryStatus.CREATED);

        UpdateDeliveryRequest request = new UpdateDeliveryRequest();
        request.setCourierId("courier456");
        request.setStatus(DeliveryStatus.IN_PROGRESS);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(existingDelivery));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DeliveryResponse result = deliveryService.updateDelivery(deliveryId, request);

        assertNotNull(result);
        assertEquals(deliveryId, result.getId());
        assertEquals("courier456", result.getCourierId());
        assertEquals(DeliveryStatus.IN_PROGRESS, result.getStatus());
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void testUpdateDelivery_DeliveryNotFound() {
        Long deliveryId = 1L;
        UpdateDeliveryRequest request = new UpdateDeliveryRequest();
        request.setStatus(DeliveryStatus.IN_PROGRESS);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

        assertThrows(DeliveryNotFoundException.class, () -> deliveryService.updateDelivery(deliveryId, request));
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).save(any(Delivery.class));
    }
}
