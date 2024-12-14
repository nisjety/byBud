import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getBidsForDelivery } from '../services/DeliveryService';

const useBids = (deliveryId) => {
    const [bids, setBids] = useState([]);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!deliveryId) return;

        const fetchBids = async () => {
            try {
                const bidsData = await getBidsForDelivery(Number(deliveryId));
                setBids(bidsData);
            } catch (err) {
                setError(err);
            }
        };

        fetchBids();
    }, [deliveryId]);

    return { bids, error };
};

const BidList = () => {
    const { deliveryId } = useParams();
    const { bids, error } = useBids(deliveryId);

    if (!deliveryId) {
        return <div>Delivery ID is missing!</div>;
    }

    if (error) {
        return <div>Failed to fetch bids: {error.message}</div>;
    }

    return (
        <div>
            <h2>Bids for Delivery {deliveryId}</h2>
            {bids.length === 0 ? (
                <p>No bids available for this delivery.</p>
            ) : (
                <ul>
                    {bids.map((bid) => (
                        <li key={bid.id}>
                            <strong>Courier:</strong> {bid.courierId || 'Unknown'} <br />
                            <strong>Note:</strong> {bid.note || 'No note provided'}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default BidList;
