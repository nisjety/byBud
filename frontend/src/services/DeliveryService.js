import { DeliveryAPI } from "./APIUtility";

const DELIVERY_API_URL = "/delivery/";

export const createDelivery = async (deliveryDetails) => {
    const response = await DeliveryAPI.post(DELIVERY_API_URL, deliveryDetails);
    return response.data; // Align with CreateDeliveryResponse DTO
};

export const getDeliveriesForCustomer = async (customerId) => {
    const response = await DeliveryAPI.get(`${DELIVERY_API_URL}customer/${customerId}`);
    return response.data;
};

export const getBidsForDelivery = async (deliveryId) => {
    const response = await DeliveryAPI.get(`${DELIVERY_API_URL}${deliveryId}/bids`);
    return response.data;
};

export const placeBid = async (bidData) => {
    const response = await DeliveryAPI.post(`${DELIVERY_API_URL}bid`, bidData);
    return response.data;
};
