package com.bybud.deliveryservice.repository;

import com.bybud.deliveryservice.model.CourierBid;
import com.bybud.common.model.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourierBidRepository extends JpaRepository<CourierBid, Long> {

    // Find bids by delivery ID
    List<CourierBid> findByDeliveryId(Long deliveryId);

    // Find bids by courier ID
    List<CourierBid> findByCourierId(String courierId);

    // Find bids by status
    List<CourierBid> findByStatus(BidStatus status);

    // Find a specific bid by delivery ID and courier ID
    CourierBid findByDeliveryIdAndCourierId(Long deliveryId, String courierId);
}
