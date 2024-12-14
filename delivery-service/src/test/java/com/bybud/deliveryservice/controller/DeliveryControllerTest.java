package com.bybud.deliveryservice.controller;

import com.bybud.common.model.DeliveryStatus;
import com.bybud.deliveryservice.controller.DeliveryController;
import com.bybud.deliveryservice.dto.CreateDeliveryRequest;
import com.bybud.deliveryservice.dto.DeliveryResponse;
import com.bybud.deliveryservice.dto.UpdateDeliveryRequest;
import com.bybud.deliveryservice.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeliveryControllerTest {

    private DeliveryController deliveryController;

    @Mock
    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        deliveryController = new DeliveryController(deliveryService);
    }

    @Test
    void testCreateDelivery_Success() {
        CreateDeliveryRequest request = new CreateDeliveryRequest();
        request.setCustomerId("customer123");
        request.setDeliveryDetails("Package description");

        DeliveryResponse responseMock = new DeliveryResponse();
        responseMock.setId(1L);
        responseMock.setCustomerId("customer123");
        responseMock.setDeliveryDetails("Package description");
        responseMock.setStatus(DeliveryStatus.CREATED);

        when(deliveryService.createDelivery(any(CreateDeliveryRequest.class))).thenReturn(responseMock);

        ResponseEntity<DeliveryResponse> response = deliveryController.createDelivery(request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("customer123", response.getBody().getCustomerId());
        verify(deliveryService).createDelivery(any(CreateDeliveryRequest.class));
    }

    @Test
    void testUpdateDeliveryStatus_Success() {
        Long deliveryId = 1L;
        UpdateDeliveryRequest request = new UpdateDeliveryRequest();
        request.setStatus(DeliveryStatus.IN_PROGRESS);
        request.setCourierId("courier456");

        DeliveryResponse responseMock = new DeliveryResponse();
        responseMock.setId(deliveryId);
        responseMock.setStatus(DeliveryStatus.IN_PROGRESS);
        responseMock.setCourierId("courier456");

        when(deliveryService.updateDelivery(eq(deliveryId), any(UpdateDeliveryRequest.class)))
                .thenReturn(responseMock);

        ResponseEntity<DeliveryResponse> response = deliveryController.updateDelivery(deliveryId, request);


        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(DeliveryStatus.IN_PROGRESS, response.getBody().getStatus());
        verify(deliveryService).updateDelivery(eq(deliveryId), any(UpdateDeliveryRequest.class));
    }

    @Test
    void testUpdateDeliveryStatus_NotFound() {
        Long deliveryId = 1L;
        UpdateDeliveryRequest request = new UpdateDeliveryRequest();
        request.setStatus(DeliveryStatus.IN_PROGRESS);

        when(deliveryService.updateDelivery(eq(deliveryId), any(UpdateDeliveryRequest.class)))
                .thenThrow(new IllegalArgumentException("Delivery not found"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                deliveryController.updateDelivery(deliveryId, request));


        assertEquals("Delivery not found", exception.getMessage());
        verify(deliveryService).updateDelivery(eq(deliveryId), any(UpdateDeliveryRequest.class));
    }
}
