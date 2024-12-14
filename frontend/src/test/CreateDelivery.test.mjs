import React from 'react';
import { render, screen, waitFor, within } from '@testing-library/react';
import DeliveryList from '../pages/DeliveryList';
import { getDeliveriesForCustomer } from '../services/DeliveryService';
import { BrowserRouter } from 'react-router-dom';
import { jest } from '@jest/globals';

jest.mock('../services/DeliveryService', () => ({
    getDeliveriesForCustomer: jest.fn(),
}));

const mockDeliveries = [
    {
        id: 1,
        deliveryDetails: 'Deliver package to 123 Main St',
        status: 'PENDING',
    },
    {
        id: 2,
        deliveryDetails: 'Deliver documents to 456 Oak Ave',
        status: 'COMPLETED',
    },
];

describe('DeliveryList Component', () => {
    const mockToken = 'mockToken';
    const mockCustomerId = '123';

    beforeEach(() => {
        localStorage.setItem('token', mockToken); // Mock the token
    });

    afterEach(() => {
        jest.clearAllMocks(); // Clear mocks after each test
    });

    test('renders deliveries successfully', async () => {
        getDeliveriesForCustomer.mockResolvedValueOnce(mockDeliveries);

        render(
            <BrowserRouter>
                <DeliveryList customerId={mockCustomerId} />
            </BrowserRouter>
        );

        // Check for the loading state
        expect(screen.getByText(/Loading deliveries/i)).toBeInTheDocument();

        // Wait for deliveries to load
        await waitFor(() => {
            const listItems = screen.getAllByRole('listitem');
            expect(listItems).toHaveLength(mockDeliveries.length);

            // Assert details of each delivery
            expect(within(listItems[0]).getByText(/123 Main St/i)).toBeInTheDocument();
            expect(within(listItems[0]).getByText(/PENDING/i)).toBeInTheDocument();
            expect(within(listItems[1]).getByText(/456 Oak Ave/i)).toBeInTheDocument();
            expect(within(listItems[1]).getByText(/COMPLETED/i)).toBeInTheDocument();
        });
    });

    test('shows error message on API failure', async () => {
        getDeliveriesForCustomer.mockRejectedValueOnce(new Error('Network error'));

        render(
            <BrowserRouter>
                <DeliveryList customerId={mockCustomerId} />
            </BrowserRouter>
        );

        // Check for the loading state
        expect(screen.getByText(/Loading deliveries/i)).toBeInTheDocument();

        // Wait for the error message
        await waitFor(() => {
            expect(screen.getByText(/Failed to fetch deliveries/i)).toBeInTheDocument();
            expect(screen.getByText(/Network error/i)).toBeInTheDocument();
        });
    });

    test('displays appropriate structure for deliveries', async () => {
        getDeliveriesForCustomer.mockResolvedValueOnce(mockDeliveries);

        render(
            <BrowserRouter>
                <DeliveryList customerId={mockCustomerId} />
            </BrowserRouter>
        );

        // Wait for the deliveries
        await waitFor(() => {
            const deliveryList = screen.getByRole('list');
            const items = within(deliveryList).getAllByRole('listitem');

            expect(items).toHaveLength(mockDeliveries.length);

            mockDeliveries.forEach((delivery, index) => {
                expect(items[index]).toHaveTextContent(delivery.deliveryDetails);
                expect(items[index]).toHaveTextContent(delivery.status);
            });
        });
    });
});
