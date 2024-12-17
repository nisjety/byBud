import React, { useEffect, useState } from "react";
import { getDeliveriesForCustomer } from "../services/DeliveryService";

const DeliveryList = () => {
    const [deliveries, setDeliveries] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchDeliveries = async () => {
            try {
                const customerId = localStorage.getItem("customerId");
                if (!customerId) throw new Error("Customer ID not found. Please log in again.");

                const data = await getDeliveriesForCustomer(customerId);
                setDeliveries(data);
            } catch (err) {
                setError(`Failed to fetch deliveries: ${err.message}`);
            } finally {
                setLoading(false);
            }
        };

        fetchDeliveries();
    }, []);

    if (loading) return <p>Loading deliveries...</p>;
    if (error) return <p style={{ color: "red" }}>{error}</p>;

    return (
        <div>
            <h2>Your Deliveries</h2>
            {deliveries.length === 0 ? (
                <p>No deliveries found.</p>
            ) : (
                <ul>
                    {deliveries.map((delivery) => (
                        <li key={delivery.id}>
                            <strong>Details:</strong> {delivery.deliveryDetails} <br />
                            <strong>Status:</strong> {delivery.status} <br />
                            <strong>Courier:</strong> {delivery.courierId || "Not assigned"} <br />
                            <strong>Created:</strong>{" "}
                            {delivery.createdDate ? new Date(delivery.createdDate).toLocaleString() : "Unknown"}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default DeliveryList;
