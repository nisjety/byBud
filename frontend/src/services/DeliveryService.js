import axios from "axios";

const DELIVERY_API_BASE = "http://localhost:8082/api/delivery";

// Get deliveries for a specific customer
export const getDeliveriesForCustomer = async (customerId) => {
    const response = await axios.get(`${DELIVERY_API_BASE}/customer/${customerId}`);
    return response.data.data; // Access the 'data' field from the API response
};

// Get deliveries for a specific courier
export const getDeliveriesForCourier = async (courierId) => {
    const response = await axios.get(`${DELIVERY_API_BASE}/courier/${courierId}`);
    return response.data.data; // Access the 'data' field from the API response
};

// Create a new delivery
export const createDelivery = async (deliveryData) => {
    const response = await axios.post(`${DELIVERY_API_BASE}`, deliveryData, {
        headers: { "Content-Type": "application/json" },
    });
    return response.data.data;
};

// Accept a delivery (Courier)
export const acceptDelivery = async (deliveryId, courierId) => {
    const response = await axios.put(`${DELIVERY_API_BASE}/${deliveryId}/accept/${courierId}`);
    return response.data.data;
};

// Update delivery status
export const updateDeliveryStatus = async (deliveryId, status, userId) => {
    const response = await axios.put(`${DELIVERY_API_BASE}/${deliveryId}/status`, null, {
        params: { status, userId },
    });
    return response.data.data;
};

// Get all deliveries
export const getAllDeliveries = async () => {
    const response = await axios.get(`${DELIVERY_API_BASE}`);
    return response.data.data;
};
