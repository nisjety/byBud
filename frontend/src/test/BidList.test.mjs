import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import BidList from '../pages/BidList.jsx';
import { getBidsForDelivery } from '../services/DeliveryService.js';
import { test, expect } from '@jest/globals';


jest.mock('../services/DeliveryService.js', () => ({
    getBidsForDelivery: jest.fn(),
}));

const mockBids = [
    { id: 1, courierId: 'courier123', note: 'Will deliver soon' },
    { id: 2, courierId: 'courier456', note: 'Can deliver in 2 hours' },
];

test('renders bids successfully', async () => {
    getBidsForDelivery.mockResolvedValueOnce(mockBids);

    render(
        <MemoryRouter initialEntries={['/bids/1']}>
            <Routes>
                <Route path="/bids/:deliveryId" element={<BidList />} />
            </Routes>
        </MemoryRouter>
    );

    expect(screen.getByText(/Bids for Delivery 1/i)).toBeInTheDocument();

    await waitFor(() => {
        expect(screen.getByText(/courier123/i)).toBeInTheDocument();
        expect(screen.getByText(/Will deliver soon/i)).toBeInTheDocument();
        expect(screen.getByText(/courier456/i)).toBeInTheDocument();
        expect(screen.getByText(/Can deliver in 2 hours/i)).toBeInTheDocument();
    });
});
