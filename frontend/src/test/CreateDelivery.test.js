import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import CreateDelivery from "../pages/CreateDelivery";
import { createDelivery } from "../services/DeliveryService";

jest.mock("../services/DeliveryService", () => ({
    createDelivery: jest.fn(),
}));

describe("CreateDelivery Component", () => {
    beforeEach(() => {
        localStorage.setItem("customerId", "123");
    });

    afterEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
    });

    test("renders the form successfully", () => {
        render(<CreateDelivery />);

        expect(
            screen.getByRole("heading", { name: /Create Delivery Request/i })
        ).toBeInTheDocument();
        expect(
            screen.getByPlaceholderText(/Enter delivery details/i)
        ).toBeInTheDocument();
        expect(
            screen.getByRole("button", { name: /Create Delivery/i })
        ).toBeInTheDocument();
    });

    test("submits the form successfully", async () => {
        createDelivery.mockResolvedValueOnce();

        render(<CreateDelivery />);

        const textarea = screen.getByPlaceholderText(/Enter delivery details/i);
        const submitButton = screen.getByRole("button", { name: /Create Delivery/i });

        fireEvent.change(textarea, { target: { value: "Deliver package to 123 Main St" } });
        fireEvent.click(submitButton);

        await waitFor(() => {
            expect(createDelivery).toHaveBeenCalledWith({
                customerId: "123",
                deliveryDetails: "Deliver package to 123 Main St",
            });
        });
    });

    test("shows error message if API call fails", async () => {
        createDelivery.mockRejectedValueOnce(new Error("Network error"));

        render(<CreateDelivery />);

        const textarea = screen.getByPlaceholderText(/Enter delivery details/i);
        const submitButton = screen.getByRole("button", { name: /Create Delivery/i });

        fireEvent.change(textarea, { target: { value: "Deliver package to 123 Main St" } });
        fireEvent.click(submitButton);

        await waitFor(() => {
            // Check for the exact error message displayed
            expect(screen.getByText(/Network error/i)).toBeInTheDocument();
        });
    });


    test("shows error if customerId is missing", () => {
        localStorage.removeItem("customerId");

        render(<CreateDelivery />);

        const textarea = screen.getByPlaceholderText(/Enter delivery details/i);
        const submitButton = screen.getByRole("button", { name: /Create Delivery/i });

        fireEvent.change(textarea, { target: { value: "Deliver package to 123 Main St" } });
        fireEvent.click(submitButton);

        expect(screen.getByText(/Customer ID not found/i)).toBeInTheDocument();
    });
});
