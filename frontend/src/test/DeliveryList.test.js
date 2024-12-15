import React from 'react';
import { render, screen, waitFor, within } from '@testing-library/react';
import DeliveryList from '../pages/DeliveryList';
import { getDeliveriesForCustomer } from '../services/DeliveryService';
import { BrowserRouter } from 'react-router-dom';

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
    beforeEach(() => {
        localStorage.setItem('customerId', '123'); // Set customerId in localStorage
    });

    afterEach(() => {
        jest.clearAllMocks(); // Clear mocks after each test
        localStorage.clear(); // Clear localStorage after each test
    });

    test('renders deliveries successfully', async () => {
        getDeliveriesForCustomer.mockResolvedValueOnce(mockDeliveries);

        render(
            <BrowserRouter>
                <DeliveryList />
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
                <DeliveryList />
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
                <DeliveryList />
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

    test('shows error if customerId is missing', () => {
        localStorage.removeItem('customerId'); // Remove customerId to simulate the missing condition

        render(
            <BrowserRouter>
                <DeliveryList />
            </BrowserRouter>
        );

        // Wait for the error message to appear
        expect(screen.getByText(/Customer ID not found/i)).toBeInTheDocument();
    });
});
