import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import DeliveryList from '../pages/DeliveryList';
import { getDeliveriesForCustomer } from '../services/DeliveryService';
import { vi } from 'vitest';

vi.mock('../services/DeliveryService', () => ({
    getDeliveriesForCustomer: vi.fn(),
}));

const mockDeliveries = [
    { id: 1, deliveryDetails: 'Package to 123 Main St', status: 'PENDING' },
    { id: 2, deliveryDetails: 'Documents to 456 Oak Ave', status: 'COMPLETED' },
];

test('renders deliveries successfully', async () => {
    getDeliveriesForCustomer.mockResolvedValueOnce(mockDeliveries);

    render(
        <BrowserRouter>
            <DeliveryList />
        </BrowserRouter>
    );

    expect(screen.getByText(/Loading deliveries/i)).toBeInTheDocument();

    await waitFor(() => {
        expect(screen.getByText(/Package to 123 Main St/i)).toBeInTheDocument();
        expect(screen.getByText(/Documents to 456 Oak Ave/i)).toBeInTheDocument();
    });
});
