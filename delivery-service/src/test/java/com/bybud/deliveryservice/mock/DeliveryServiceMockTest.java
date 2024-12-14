package com.bybud.deliveryservice.mock;

import com.bybud.deliveryservice.dto.*;
import com.bybud.deliveryservice.service.*;
import com.bybud.deliveryservice.model.CourierBid;
import com.bybud.deliveryservice.model.Delivery;
import com.bybud.deliveryservice.repository.CourierBidRepository;
import com.bybud.deliveryservice.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeliveryServiceMockTest {

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
    void testCreateDelivery() {
        CreateDeliveryRequest request = new CreateDeliveryRequest();
        request.setCustomerId("customer123");
        request.setDeliveryDetails("Deliver documents to 123 Main St");

        Delivery delivery = new Delivery("customer123", "Deliver documents to 123 Main St");
        delivery.setId(1L);

        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        DeliveryResponse response = deliveryService.createDelivery(request);

        assertNotNull(response);
        assertEquals("customer123", response.getCustomerId());
        assertEquals("Deliver documents to 123 Main St", response.getDeliveryDetails());
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void testGetDeliveriesForCustomer() {
        List<Delivery> mockDeliveries = new ArrayList<>();
        Delivery delivery = new Delivery("customer123", "Delivery 1");
        delivery.setId(1L);
        mockDeliveries.add(delivery);

        when(deliveryRepository.findByCustomerId("customer123")).thenReturn(mockDeliveries);

        List<DeliveryResponse> responses = deliveryService.getDeliveriesForCustomer("customer123");

        assertEquals(1, responses.size());
        assertEquals("Delivery 1", responses.getFirst().getDeliveryDetails());
        verify(deliveryRepository).findByCustomerId("customer123");
    }

    @Test
    void testGetBidsForDelivery() {
        List<CourierBid> mockBids = new ArrayList<>();
        CourierBid bid = new CourierBid(1L, "courier123", "Will deliver soon");
        mockBids.add(bid);

        when(courierBidRepository.findByDeliveryId(1L)).thenReturn(mockBids);

        List<CourierBid> bids = deliveryService.getBidsForDelivery(1L);

        assertEquals(1, bids.size());
        assertEquals("courier123", bids.getFirst().getCourierId());
        verify(courierBidRepository).findByDeliveryId(1L);
    }
}
