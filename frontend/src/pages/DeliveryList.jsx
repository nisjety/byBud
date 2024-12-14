import { useEffect, useState } from 'react';
import { getDeliveriesForCustomer } from '../services/DeliveryService';
import { useNavigate } from 'react-router-dom';

const useCustomerDeliveries = () => {
    const [deliveries, setDeliveries] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchDeliveries = async () => {
            const token = localStorage.getItem('token');
            const customerId = localStorage.getItem('customerId'); // Ensure this is set after login
            if (!token || !customerId) {
                navigate('/login');
                return;
            }

            try {
                const deliveriesData = await getDeliveriesForCustomer(customerId, token);
                setDeliveries(deliveriesData);
            } catch (error) {
                setError(error.response?.data?.message || error.message);
            } finally {
                setLoading(false);
            }
        };

        fetchDeliveries();
    }, [navigate]);

    return { deliveries, error, loading };
};

const DeliveryList = () => {
    const { deliveries, error, loading } = useCustomerDeliveries();

    if (loading) {
        return <div>Loading deliveries...</div>;
    }

    return (
        <div>
            <h2>Your Deliveries</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {deliveries.length === 0 ? (
                <p>No deliveries found.</p>
            ) : (
                <ul>
                    {deliveries.map((delivery) => (
                        <li key={delivery.id}>
                            <strong>{delivery.deliveryDetails || 'No details available'}</strong> - Status: {delivery.status}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};


export default DeliveryList;
