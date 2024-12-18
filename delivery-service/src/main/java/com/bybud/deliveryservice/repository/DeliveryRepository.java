package com.bybud.deliveryservice.repository;

import com.bybud.common.model.DeliveryStatus;
import com.bybud.deliveryservice.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    // Find deliveries by customerId
    List<Delivery> findByCustomerId(Long customerId);

    // Find deliveries by courierId
    List<Delivery> findByCourierId(Long courierId);


    // Find deliveries by status
    List<Delivery> findByStatus(DeliveryStatus status);

    // Find a delivery by delivery address
    Optional<Delivery> findByDeliveryAddress(String deliveryAddress);

    // Find a delivery by pickup address
    Optional<Delivery> findByPickupAddress(String pickupAddress);

    // Find a delivery by ID and status
    Optional<Delivery> findByIdAndStatus(Long id, DeliveryStatus status);

    // Find deliveries by customerId and status
    List<Delivery> findByCustomerIdAndStatus(Long customerId, DeliveryStatus status);

    // Find deliveries by courierId and status
    List<Delivery> findByCourierIdAndStatus(Long courierId, DeliveryStatus status);

    // Check if a delivery is assigned to a given courier
    boolean existsByIdAndCourierId(Long id, Long courierId);
}
