package com.bybud.common.dto;

import com.bybud.common.model.DeliveryStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateDeliveryRequest {

    @NotNull(message = "Delivery ID is required.")
    private Long deliveryId;
    private Long courierId;

    @NotNull(message = "Delivery status is required.")
    private DeliveryStatus status;

    public Long getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Long deliveryId) { this.deliveryId = deliveryId; }

    public Long getCourierId() { return courierId; }
    public void setCourierId(Long courierId) { this.courierId = courierId; }

    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }
}
