package com.bybud.deliveryservice.repository;

import com.bybud.common.model.DeliveryStatus;
import com.bybud.common.model.User;
import com.bybud.deliveryservice.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    // Find deliveries by customer
    List<Delivery> findByCustomer(User customer);

    // Find deliveries by courier
    List<Delivery> findByCourier(User courier);

    // Find deliveries by status
    List<Delivery> findByStatus(DeliveryStatus status);

    // Find a delivery by delivery address
    Optional<Delivery> findByDeliveryAddress(String deliveryAddress);

    // Find a delivery by pickup address
    Optional<Delivery> findByPickupAddress(String pickupAddress);

    // Find a delivery by ID and status
    Optional<Delivery> findByIdAndStatus(Long id, DeliveryStatus status);

    // Find deliveries by customer and status
    List<Delivery> findByCustomerAndStatus(User customer, DeliveryStatus status);

    // Find deliveries by courier and status
    List<Delivery> findByCourierAndStatus(User courier, DeliveryStatus status);

    // Check if a delivery is assigned to a given courier
    boolean existsByIdAndCourier(Long id, User courier);
}
