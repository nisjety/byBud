import React, { useEffect, useState } from "react";
import { getDeliveriesForCustomer } from "../services/DeliveryService";
import { getUserById } from "../services/UserService";

const UserProfile = () => {
    const [user, setUser] = useState(null);
    const [deliveries, setDeliveries] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    const customerId = localStorage.getItem("userId");

    useEffect(() => {
        const fetchUserAndDeliveries = async () => {
            try {
                if (!customerId) throw new Error("User ID not found.");

                // Fetch the user data
                const userData = await getUserById(Number(customerId));
                setUser(userData);

                // Fetch user's deliveries
                const data = await getDeliveriesForCustomer(Number(customerId));
                setDeliveries(data || []);
            } catch (err) {
                setError(err.response?.data?.message || err.message || "Failed to fetch your profile data.");
            } finally {
                setLoading(false);
            }
        };

        fetchUserAndDeliveries();
    }, [customerId]);

    if (loading) return <p>Loading your profile...</p>;
    if (error) return <p style={{ color: "red" }}>{error}</p>;

    return (
        <div>
            <h2>User Profile</h2>
            {user ? (
                <div style={{ marginBottom: "20px" }}>
                    <p><strong>Full Name:</strong> {user.fullName}</p>
                    <p><strong>Email:</strong> {user.email}</p>
                    <p><strong>Phone Number:</strong> {user.phoneNumber}</p>
                    <p><strong>Active:</strong> {user.active ? "Yes" : "No"}</p>
                    <p><strong>Date of Birth:</strong> {user.dateOfBirth || "N/A"}</p>
                    <p><strong>Roles:</strong> {user.roles ? user.roles.join(", ") : "None"}</p>
                </div>
            ) : (
                <p>User details not found.</p>
            )}

            <h3>Your Deliveries</h3>
            {deliveries.length === 0 ? (
                <p>No deliveries found.</p>
            ) : (
                <ul>
                    {deliveries.map((delivery) => (
                        <li key={delivery.id} style={{ marginBottom: "15px" }}>
                            <strong>Details:</strong> {delivery.deliveryDetails || "N/A"} <br />
                            <strong>Pickup Address:</strong> {delivery.pickupAddress || "N/A"} <br />
                            <strong>Delivery Address:</strong> {delivery.deliveryAddress || "N/A"} <br />
                            <strong>Status:</strong> {delivery.status || "Unknown"} <br />
                            <strong>Created:</strong>{" "}
                            {delivery.createdDate
                                ? new Date(delivery.createdDate).toLocaleString()
                                : "Unknown"}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default UserProfile;
