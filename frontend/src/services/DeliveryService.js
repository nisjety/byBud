import axios from "axios";

const API_URL = "/api/delivery/";

export const getDeliveriesForCustomer = async (token, customerId) => {
    const response = await axios.get(`${API_URL}customer/${customerId}`, {
        headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
};

export const createDelivery = async (deliveryDetails, token) => {
    const response = await axios.post(API_URL, deliveryDetails, {
        headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
};

export const getBidsForDelivery = async (deliveryId, token) => {
    const response = await axios.get(`${API_URL}${deliveryId}/bids`, {
        headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
};

