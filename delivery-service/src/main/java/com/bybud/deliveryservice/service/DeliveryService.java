package com.bybud.deliveryservice.service;

import com.bybud.common.client.DeliveryServiceClient;
import com.bybud.common.dto.CreateDeliveryRequest;
import com.bybud.common.dto.DeliveryResponse;
import com.bybud.common.dto.UserDTO;
import com.bybud.common.model.DeliveryStatus;
import com.bybud.common.model.RoleName;
import com.bybud.deliveryservice.model.Delivery;
import com.bybud.deliveryservice.repository.DeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryService.class);

    private final DeliveryRepository deliveryRepository;
    private final DeliveryServiceClient deliveryServiceClient;

    public DeliveryService(DeliveryRepository deliveryRepository, DeliveryServiceClient deliveryServiceClient) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryServiceClient = deliveryServiceClient;
    }

    public List<DeliveryResponse> getAllDeliveries(Long userId) {
        // If needed, validate role if you want. Otherwise, just return all deliveries.
        return deliveryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DeliveryResponse createDelivery(CreateDeliveryRequest request) {
        // Must be CUSTOMER to create a delivery
        UserDTO customer = deliveryServiceClient.getUserById(request.getCustomerId());
        logger.debug("Fetched user for createDelivery: {}", customer);
        validateUserRole(customer, RoleName.CUSTOMER);

        Delivery delivery = new Delivery();
        delivery.setCustomerId(customer.getId());
        delivery.setDeliveryDetails(request.getDeliveryDetails());
        delivery.setPickupAddress(request.getPickupAddress());
        delivery.setDeliveryAddress(request.getDeliveryAddress());
        delivery.setStatus(DeliveryStatus.CREATED);

        Delivery savedDelivery = deliveryRepository.save(delivery);
        return mapToResponse(savedDelivery);
    }

    public List<DeliveryResponse> getDeliveriesForCustomer(Long customerId) {
        // Must be CUSTOMER to fetch customer deliveries
        UserDTO customer = deliveryServiceClient.getUserById(customerId);
        logger.debug("Fetched user for getDeliveriesForCustomer: {}", customer);
        validateUserRole(customer, RoleName.CUSTOMER);

        return deliveryRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<DeliveryResponse> getDeliveriesForCourier(Long courierId) {
        // Must be COURIER to fetch courier deliveries
        UserDTO courier = deliveryServiceClient.getUserById(courierId);
        logger.debug("Fetched user for getDeliveriesForCourier: {}", courier);
        validateUserRole(courier, RoleName.COURIER);

        // Ensure you have a finder method in your DeliveryRepository: findByCourierId
        return deliveryRepository.findByCourierId(courierId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DeliveryResponse acceptDelivery(Long deliveryId, Long courierId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found with ID: " + deliveryId));

        UserDTO courier = deliveryServiceClient.getUserById(courierId);
        validateUserRole(courier, RoleName.COURIER);

        if (delivery.getStatus() != DeliveryStatus.CREATED) {
            throw new IllegalArgumentException("Delivery cannot be accepted. Current status: " + delivery.getStatus());
        }

        delivery.setCourierId(courier.getId());
        delivery.setStatus(DeliveryStatus.ASSIGNED);

        return mapToResponse(deliveryRepository.save(delivery));
    }

    public DeliveryResponse updateDeliveryStatus(Long deliveryId, DeliveryStatus status, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required to update delivery status.");
        }

        UserDTO user = deliveryServiceClient.getUserById(userId);
        validateUserRole(user, RoleName.COURIER);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found with ID: " + deliveryId));

        delivery.setStatus(status);
        return mapToResponse(deliveryRepository.save(delivery));
    }

    private void validateUserRole(UserDTO user, RoleName requiredRole) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User ID is null. Cannot validate role.");
        }
        Set<RoleName> userRoles = user.getRoles();
        logger.debug("Validating user {} roles: {}", user.getId(), userRoles);
        if (userRoles == null || !userRoles.contains(requiredRole)) {
            throw new IllegalArgumentException("User with ID: " + user.getId() +
                    " does not have the required role: " + requiredRole + ".");
        }
    }

    private DeliveryResponse mapToResponse(Delivery delivery) {
        DeliveryResponse response = new DeliveryResponse();
        response.setId(delivery.getId());
        response.setCustomerId(delivery.getCustomerId());
        response.setCustomerName(fetchCustomerName(delivery.getCustomerId()));
        response.setCourierId(delivery.getCourierId());
        response.setCourierUsername(fetchCourierUsername(delivery.getCourierId()));
        response.setDeliveryDetails(delivery.getDeliveryDetails());
        response.setPickupAddress(delivery.getPickupAddress());
        response.setDeliveryAddress(delivery.getDeliveryAddress());
        response.setStatus(delivery.getStatus());
        response.setCreatedDate(delivery.getCreatedDate());
        response.setUpdatedDate(delivery.getUpdatedDate());

        return response;
    }

    private String fetchCustomerName(Long customerId) {
        if (customerId == null) return null;
        try {
            UserDTO customerDTO = deliveryServiceClient.getUserById(customerId);
            return customerDTO.getFullName();
        } catch (Exception e) {
            logger.warn("Unable to fetch customer details for ID: {}", customerId, e);
            return null;
        }
    }

    private String fetchCourierUsername(Long courierId) {
        if (courierId == null) return null;
        try {
            UserDTO courierDTO = deliveryServiceClient.getUserById(courierId);
            return courierDTO.getUsername();
        } catch (Exception e) {
            logger.warn("Unable to fetch courier details for ID: {}", courierId, e);
            return null;
        }
    }
}
