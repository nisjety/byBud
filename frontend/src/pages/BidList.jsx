import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getBidsForDelivery } from "../services/DeliveryService";

const BidList = () => {
    const { deliveryId } = useParams();
    const [bids, setBids] = useState([]);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!deliveryId) return;

        const fetchBids = async () => {
            try {
                const bidsData = await getBidsForDelivery(deliveryId); // Matches BidRequest
                setBids(bidsData);
            } catch (err) {
                setError(err.message || "Failed to fetch bids.");
            }
        };

        fetchBids();
    }, [deliveryId]);

    if (!deliveryId) return <div>Delivery ID is missing!</div>;

    if (error) return <div style={{ color: "red" }}>Error: {error}</div>;

    return (
        <div>
            <h2>Bids for Delivery {deliveryId}</h2>
            {bids.length === 0 ? (
                <p>No bids available for this delivery.</p>
            ) : (
                <ul>
                    {bids.map((bid) => (
                        <li key={bid.id} data-testid={`bid-item-${bid.id}`}>
                            <strong>Courier:</strong> {bid.courierId || "Unknown"} <br />
                            <strong>Note:</strong> {bid.note || "No note provided"}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default BidList;
