import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import BidList from "../pages/BidList";
import { getBidsForDelivery } from "../services/DeliveryService";

jest.mock("../services/DeliveryService", () => ({
    getBidsForDelivery: jest.fn(),
}));

const mockBids = [
    { id: 1, courierId: "courier123", note: "Will deliver soon" },
    { id: 2, courierId: "courier456", note: "Can deliver in 2 hours" },
];

test("renders bids successfully", async () => {
    getBidsForDelivery.mockResolvedValueOnce(mockBids);

    render(
        <MemoryRouter initialEntries={["/bids/1"]}>
            <Routes>
                <Route path="/bids/:deliveryId" element={<BidList />} />
            </Routes>
        </MemoryRouter>
    );

    // Check if header renders correctly
    expect(screen.getByText(/Bids for Delivery 1/i)).toBeInTheDocument();

    // Wait for the mock data to appear
    await waitFor(() => {
        expect(screen.getByText(/courier123/i)).toBeInTheDocument();
        expect(screen.getByText(/Will deliver soon/i)).toBeInTheDocument();
        expect(screen.getByText(/courier456/i)).toBeInTheDocument();
        expect(screen.getByText(/Can deliver in 2 hours/i)).toBeInTheDocument();
    });
});

test("renders no bids message when no bids are available", async () => {
    getBidsForDelivery.mockResolvedValueOnce([]);

    render(
        <MemoryRouter initialEntries={["/bids/2"]}>
            <Routes>
                <Route path="/bids/:deliveryId" element={<BidList />} />
            </Routes>
        </MemoryRouter>
    );

    // Check if header renders correctly
    expect(screen.getByText(/Bids for Delivery 2/i)).toBeInTheDocument();

    // Wait for the no bids message to appear
    await waitFor(() => {
        expect(screen.getByText(/No bids available for this delivery./i)).toBeInTheDocument();
    });
});

test("handles missing deliveryId gracefully", () => {
    render(
        <MemoryRouter initialEntries={["/"]}>
            <Routes>
                {/* Render the BidList component for the root path */}
                <Route path="/" element={<BidList />} />
            </Routes>
        </MemoryRouter>
    );

    // Check if the "Delivery ID is missing!" message is displayed
    expect(screen.getByText(/Delivery ID is missing!/i)).toBeInTheDocument();
});

test("displays an error message on API failure", async () => {
    getBidsForDelivery.mockRejectedValueOnce(new Error("API error"));

    render(
        <MemoryRouter initialEntries={["/bids/3"]}>
            <Routes>
                <Route path="/bids/:deliveryId" element={<BidList />} />
            </Routes>
        </MemoryRouter>
    );

    // Wait for the error message to appear
    await waitFor(() => {
        expect(screen.getByText(/Error: API error/i)).toBeInTheDocument();
    });
});
