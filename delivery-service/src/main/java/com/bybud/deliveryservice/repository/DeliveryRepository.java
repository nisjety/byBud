package com.bybud.deliveryservice.repository;

import com.bybud.deliveryservice.model.Delivery;
import com.bybud.common.model.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    // Find deliveries by customer ID
    List<Delivery> findByCustomerId(String customerId);

    // Find deliveries by courier ID
    List<Delivery> findByCourierId(String courierId);

    // Find deliveries by status
    List<Delivery> findByStatus(DeliveryStatus status);
}
