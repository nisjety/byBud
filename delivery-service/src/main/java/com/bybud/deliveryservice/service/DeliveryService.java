package com.bybud.deliveryservice.service;

import com.bybud.common.client.UserServiceClient;
import com.bybud.common.dto.UserDTO;
import com.bybud.common.exception.DeliveryNotFoundException;
import com.bybud.common.exception.UserNotFoundException;
import com.bybud.common.model.DeliveryStatus;
import com.bybud.common.model.RoleName;
import com.bybud.common.model.User;
import com.bybud.common.repository.UserRepository;
import com.bybud.common.security.JwtTokenProvider;
import com.bybud.deliveryservice.dto.CreateDeliveryRequest;
import com.bybud.deliveryservice.dto.DeliveryResponse;
import com.bybud.deliveryservice.model.Delivery;
import com.bybud.deliveryservice.repository.DeliveryRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UserServiceClient userServiceClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public DeliveryService(DeliveryRepository deliveryRepository,
                           UserServiceClient userServiceClient,
                           JwtTokenProvider jwtTokenProvider,
                           UserRepository userRepository) {
        this.deliveryRepository = deliveryRepository;
        this.userServiceClient = userServiceClient;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    public DeliveryResponse createDelivery(CreateDeliveryRequest request, String jwtToken) {
        String username = jwtTokenProvider.getUsernameFromJwt(jwtToken);
        UserDTO userDTO = userServiceClient.getUserDetails(username);

        validateRole(userDTO, RoleName.CUSTOMER, "Only customers can create deliveries.");

        User customer = getUserById(userDTO.getId());

        Delivery delivery = new Delivery();
        delivery.setCustomer(customer);
        delivery.setDeliveryDetails(request.getDeliveryDetails());
        delivery.setDeliveryAddress(request.getDeliveryAddress());
        delivery.setPickupAddress(request.getPickupAddress());
        delivery.setStatus(DeliveryStatus.CREATED);

        return mapToResponse(deliveryRepository.save(delivery));
    }

    public List<DeliveryResponse> getDeliveries(String jwtToken) {
        String username = jwtTokenProvider.getUsernameFromJwt(jwtToken);
        UserDTO userDTO = userServiceClient.getUserDetails(username);

        if (userDTO.getRoles().contains(RoleName.COURIER)) {
            return deliveryRepository.findAll().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } else if (userDTO.getRoles().contains(RoleName.CUSTOMER)) {
            User customer = getUserById(userDTO.getId());
            return deliveryRepository.findByCustomer(customer).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        throw new SecurityException("Unauthorized access.");
    }

    public List<DeliveryResponse> getAllDeliveriesForAdmin(String jwtToken) {
        String username = jwtTokenProvider.getUsernameFromJwt(jwtToken);
        UserDTO userDTO = userServiceClient.getUserDetails(username);

        validateRole(userDTO, RoleName.ADMIN, "Only admins can access all deliveries.");

        return deliveryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DeliveryResponse acceptDelivery(Long deliveryId, String jwtToken) {
        String username = jwtTokenProvider.getUsernameFromJwt(jwtToken);
        UserDTO courierDTO = userServiceClient.getUserDetails(username);

        validateRole(courierDTO, RoleName.COURIER, "Only couriers can accept deliveries.");

        Delivery delivery = getDeliveryById(deliveryId);

        if (delivery.getStatus() != DeliveryStatus.CREATED) {
            throw new IllegalStateException("Delivery is already assigned or completed.");
        }

        User courier = getUserById(courierDTO.getId());
        delivery.setCourier(courier);
        delivery.setStatus(DeliveryStatus.ASSIGNED);

        return mapToResponse(deliveryRepository.save(delivery));
    }


    public DeliveryResponse updateDeliveryStatus(Long deliveryId, DeliveryStatus status, String jwtToken) {
        String username = jwtTokenProvider.getUsernameFromJwt(jwtToken);
        UserDTO courierDTO = userServiceClient.getUserDetails(username);

        validateRole(courierDTO, RoleName.COURIER, "Only couriers can update delivery statuses.");

        Delivery delivery = getDeliveryById(deliveryId);
        User courier = getUserById(courierDTO.getId());

        if (!courier.getId().equals(delivery.getCourier().getId())) {
            throw new SecurityException("You can only update your own assigned deliveries.");
        }

        delivery.setStatus(status);
        return mapToResponse(deliveryRepository.save(delivery));
    }

    private Delivery getDeliveryById(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found."));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    private void validateRole(UserDTO user, RoleName requiredRole, String errorMessage) {
        if (!user.getRoles().contains(requiredRole)) {
            throw new SecurityException(errorMessage);
        }
    }

    private DeliveryResponse mapToResponse(Delivery delivery) {
        DeliveryResponse response = new DeliveryResponse();
        response.setId(delivery.getId());
        response.setDeliveryDetails(delivery.getDeliveryDetails());
        response.setDeliveryAddress(delivery.getDeliveryAddress());
        response.setPickupAddress(delivery.getPickupAddress());
        response.setStatus(delivery.getStatus());
        response.setCreatedDate(delivery.getCreatedDate());
        response.setUpdatedDate(delivery.getUpdatedDate());
        response.setCustomerId(delivery.getCustomer().getId());

        if (delivery.getCourier() != null) {
            response.setCourierId(delivery.getCourier().getId());
            response.setCourierUsername(delivery.getCourier().getUsername());
        }
        return response;
    }
}
