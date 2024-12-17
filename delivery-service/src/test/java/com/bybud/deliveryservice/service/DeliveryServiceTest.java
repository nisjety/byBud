package com.bybud.deliveryservice.service;

import com.bybud.deliveryservice.model.Delivery;
import com.bybud.deliveryservice.repository.DeliveryRepository;
import com.bybud.common.client.UserServiceClient;
import com.bybud.common.security.JwtTokenProvider;
import com.bybud.common.repository.UserRepository;
import com.bybud.common.dto.UserDTO;
import com.bybud.common.model.RoleName;
import com.bybud.common.model.DeliveryStatus;
import com.bybud.common.model.User;
import com.bybud.deliveryservice.dto.CreateDeliveryRequest;
import com.bybud.deliveryservice.dto.DeliveryResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils; // Import this utility

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDelivery_Success() {
        // Arrange
        CreateDeliveryRequest request = new CreateDeliveryRequest();
        request.setCustomerId(1L);
        request.setDeliveryDetails("Package to Deliver");
        request.setDeliveryAddress("123 Main St");
        request.setPickupAddress("456 Warehouse Rd");

        UserDTO customerDTO = new UserDTO();
        customerDTO.setId(1L);
        customerDTO.setUsername("customer1");
        customerDTO.setRoles(Set.of(RoleName.CUSTOMER));

        User customer = mock(User.class);
        when(customer.getId()).thenReturn(1L);

        String jwtToken = "mocked-jwt-token";
        when(jwtTokenProvider.getUsernameFromJwt(jwtToken)).thenReturn("customer1");
        when(userServiceClient.getUserDetails("customer1")).thenReturn(customerDTO);
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));

        // The delivery that gets passed to save(...)
        // We’ll return it after setting the ID via reflection.
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> {
            Delivery d = invocation.getArgument(0);
            // Simulate that persistence assigned an ID of 10L
            ReflectionTestUtils.setField(d, "id", 10L);
            return d;
        });

        // Act
        DeliveryResponse response = deliveryService.createDelivery(request, jwtToken);

        // Assert
        assertNotNull(response);
        assertEquals((Long)10L, response.getId());
        assertEquals((Long)1L, response.getCustomerId());
        assertEquals(DeliveryStatus.CREATED, response.getStatus());

        verify(deliveryRepository).save(any(Delivery.class));
        verify(userRepository).findById(1L);
    }
}
