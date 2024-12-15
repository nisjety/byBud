import API from "./APIUtility";

const DELIVERY_API_URL = "/delivery/";

export const createDelivery = async (deliveryDetails) => {
    const response = await API.post(DELIVERY_API_URL, deliveryDetails); // Includes customerId and deliveryDetails
    return response.data; // Align with `DeliveryResponse`
};

export const getDeliveriesForCustomer = async (customerId) => {
    const response = await API.get(`${DELIVERY_API_URL}customer/${customerId}`);
    return response.data; // Returns `List<DeliveryResponse>`
};

export const getBidsForDelivery = async (deliveryId) => {
    const response = await API.get(`${DELIVERY_API_URL}${deliveryId}/bids`);
    return response.data; // Returns `List<BidResponse>`
};

export const placeBid = async (bidData) => {
    const response = await API.post(`${DELIVERY_API_URL}bid`, bidData); // Aligns with `BidRequest`
    return response.data; // Response could be a simple confirmation
};
