import React, { useEffect, useState } from "react";
import { getDeliveriesForCourier, acceptDelivery, updateDeliveryStatus } from "../services/DeliveryService";
import { getUserById } from "../services/UserService";

const CourierPage = () => {
    const [courierData, setCourierData] = useState(null);
    const [deliveries, setDeliveries] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [selectedStatus, setSelectedStatus] = useState({});

    useEffect(() => {
        const fetchCourierDataAndDeliveries = async () => {
            try {
                const courierId = localStorage.getItem("userId");
                if (!courierId) throw new Error("User ID not found.");

                // Fetch courier data
                const userData = await getUserById(Number(courierId));
                setCourierData(userData);

                // Fetch deliveries assigned to this courier
                const courierDeliveries = await getDeliveriesForCourier(Number(courierId));
                setDeliveries(courierDeliveries || []);
            } catch (err) {
                setError(err.response?.data?.message || err.message || "Failed to fetch courier data.");
            } finally {
                setLoading(false);
            }
        };

        fetchCourierDataAndDeliveries();
    }, []);

    const handleAcceptDelivery = async (deliveryId) => {
        try {
            const courierId = localStorage.getItem("userId");
            if (!courierId) throw new Error("User ID not found.");

            // Accept the delivery
            await acceptDelivery(deliveryId, Number(courierId));

            // Refresh deliveries after accepting
            const updatedDeliveries = await getDeliveriesForCourier(Number(courierId));
            setDeliveries(updatedDeliveries || []);
        } catch (err) {
            setError(err.response?.data?.message || err.message || "Failed to accept delivery.");
        }
    };

    const handleStatusChange = (deliveryId, newStatus) => {
        setSelectedStatus({ ...selectedStatus, [deliveryId]: newStatus });
    };

    const handleUpdateStatus = async (deliveryId) => {
        try {
            const courierId = localStorage.getItem("userId");
            if (!courierId) throw new Error("User ID not found.");

            const newStatus = selectedStatus[deliveryId];
            if (!newStatus) return;

            // Update the delivery status
            await updateDeliveryStatus(deliveryId, newStatus, Number(courierId));

            // Refresh deliveries after updating status
            const updatedDeliveries = await getDeliveriesForCourier(Number(courierId));
            setDeliveries(updatedDeliveries || []);
        } catch (err) {
            setError(err.response?.data?.message || err.message || "Failed to update delivery status.");
        }
    };

    if (loading) return <p>Loading courier data...</p>;
    if (error) return <p style={{ color: "red" }}>{error}</p>;

    const statusOptions = ["CREATED", "ASSIGNED", "IN_PROGRESS", "COMPLETED"];

    return (
        <div>
            <h2>Courier Dashboard</h2>
            {courierData && (
                <div>
                    <h3>Your Info</h3>
                    <p><strong>Full Name:</strong> {courierData.fullName}</p>
                    <p><strong>Username:</strong> {courierData.username}</p>
                    <p><strong>Email:</strong> {courierData.email}</p>
                    <p><strong>Phone:</strong> {courierData.phoneNumber}</p>
                </div>
            )}
            <div>
                <h3>Deliveries</h3>
                {deliveries.length === 0 ? (
                    <p>No deliveries assigned yet.</p>
                ) : (
                    <ul>
                        {deliveries.map((delivery) => (
                            <li key={delivery.id}>
                                <strong>Details:</strong> {delivery.deliveryDetails || "N/A"} <br />
                                <strong>Pickup Address:</strong> {delivery.pickupAddress || "N/A"} <br />
                                <strong>Delivery Address:</strong> {delivery.deliveryAddress || "N/A"} <br />
                                <strong>Status:</strong> {delivery.status || "Unknown"} <br />
                                <strong>Created:</strong> {delivery.createdDate instanceof Date
                                ? delivery.createdDate.toLocaleString()
                                : new Date(delivery.createdDate).toLocaleString()} <br />
                                {delivery.status === "CREATED" && (
                                    <button onClick={() => handleAcceptDelivery(delivery.id)}>
                                        Accept Delivery
                                    </button>
                                )}
                                {delivery.status !== "COMPLETED" && (
                                    <div style={{ marginTop: "10px" }}>
                                        <label>
                                            Update Status:
                                            <select
                                                value={selectedStatus[delivery.id] || delivery.status}
                                                onChange={(e) => handleStatusChange(delivery.id, e.target.value)}
                                            >
                                                {statusOptions.map((status) => (
                                                    <option key={status} value={status}>{status}</option>
                                                ))}
                                            </select>
                                        </label>
                                        <button onClick={() => handleUpdateStatus(delivery.id)}>
                                            Update
                                        </button>
                                    </div>
                                )}
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
};

export default CourierPage;
