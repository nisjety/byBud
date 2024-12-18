import React, { useState } from "react";
import { createDelivery } from "../services/DeliveryService";

const CreateDelivery = () => {
    const [formData, setFormData] = useState({
        deliveryDetails: "",
        pickupAddress: "",
        deliveryAddress: "",
    });
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false); // For success message

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
        setError(null); // Clear any existing errors on input change
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const userId = localStorage.getItem("userId");
            if (!userId) throw new Error("User ID not found. Please log in again.");

            const payload = {
                customerId: Number(userId), // Ensure `customerId` is a number
                deliveryDetails: formData.deliveryDetails,
                pickupAddress: formData.pickupAddress,
                deliveryAddress: formData.deliveryAddress,
            };

            // Call the API
            await createDelivery(payload);

            // On success
            setSuccess(true);
            setFormData({ deliveryDetails: "", pickupAddress: "", deliveryAddress: "" });
        } catch (err) {
            setError(err.response?.data?.message || err.message || "Failed to create delivery.");
            setSuccess(false);
        }
    };

    return (
        <div>
            <h2>Create Delivery</h2>
            {success && <p style={{ color: "green" }}>Delivery created successfully!</p>}
            {error && <p style={{ color: "red" }}>{error}</p>}
            <form onSubmit={handleSubmit}>
                <label>
                    Delivery Details:
                    <textarea
                        name="deliveryDetails"
                        value={formData.deliveryDetails}
                        onChange={handleChange}
                        placeholder="Enter delivery details"
                        required
                    />
                </label>
                <br />
                <label>
                    Pickup Address:
                    <input
                        type="text"
                        name="pickupAddress"
                        value={formData.pickupAddress}
                        onChange={handleChange}
                        placeholder="Enter pickup address"
                        required
                    />
                </label>
                <br />
                <label>
                    Delivery Address:
                    <input
                        type="text"
                        name="deliveryAddress"
                        value={formData.deliveryAddress}
                        onChange={handleChange}
                        placeholder="Enter delivery address"
                        required
                    />
                </label>
                <br />
                <button type="submit">Create</button>
            </form>
        </div>
    );
};

export default CreateDelivery;
