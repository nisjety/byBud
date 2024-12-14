import {useState} from 'react';
import {createDelivery} from '../services/DeliveryService';

const CreateDelivery = () => {
    const [deliveryDetails, setDeliveryDetails] = useState('');

    const handleChange = (e) => {
        setDeliveryDetails(e.target.value);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await createDelivery(deliveryDetails);
            alert('Delivery request created!');
        } catch (error) {
            alert('Failed to create delivery: ' + error.message);
        }
    };

    return (
        <div>
            <h2>Create Delivery Request</h2>
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