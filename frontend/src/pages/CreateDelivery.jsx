import React, { useState } from "react";
import { createDelivery } from "../services/DeliveryService";

const CreateDelivery = () => {
    const [deliveryDetails, setDeliveryDetails] = useState("");
    const [error, setError] = useState(null);

    const handleChange = (e) => setDeliveryDetails(e.target.value);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const customerId = localStorage.getItem("customerId"); // Ensure customerId is stored
            if (!customerId) throw new Error("Customer ID not found. Please log in again.");

            await createDelivery({ customerId, deliveryDetails }); // Align with CreateDeliveryRequest DTO
            alert("Delivery request created successfully!");
            setDeliveryDetails("");
        } catch (err) {
            setError(err.message || "Failed to create delivery.");
        }
    };

    return (
        <div>
            <h2>Create Delivery Request</h2>
            {error && <p style={{ color: "red" }}>{error}</p>}
            <form onSubmit={handleSubmit}>
                <textarea
                    placeholder="Enter delivery details"
                    value={deliveryDetails}
                    onChange={handleChange}
                    required
                />
                <button type="submit">Create Delivery</button>
            </form>
        </div>
    );
};

export default CreateDelivery;
