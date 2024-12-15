import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom"; // Import MemoryRouter
import Register from "../pages/Register";
import { register, getRoles } from "../services/AuthService";

jest.mock("../services/AuthService", () => ({
    register: jest.fn(),
    getRoles: jest.fn(),
}));

beforeEach(() => {
    jest.clearAllMocks();
    getRoles.mockResolvedValue(["ROLE_USER", "ROLE_COURIER"]); // Mock roles
});


describe("Register Component", () => {
    beforeEach(() => {
        jest.clearAllMocks(); // Clear mocks before each test
    });

    const renderWithRouter = (ui) => {
        return render(<MemoryRouter>{ui}</MemoryRouter>);
    };

    test("renders Register component successfully", () => {
        renderWithRouter(<Register />);

        expect(screen.getByRole("heading", { name: /Register/i })).toBeInTheDocument();
        expect(screen.getByPlaceholderText(/Username/i)).toBeInTheDocument();
        expect(screen.getByPlaceholderText(/Email/i)).toBeInTheDocument();
        expect(screen.getByPlaceholderText(/Password/i)).toBeInTheDocument();
        expect(screen.getByPlaceholderText(/Full Name/i)).toBeInTheDocument();
        expect(screen.getByPlaceholderText(/Date of Birth/i)).toBeInTheDocument();
        expect(screen.getByRole("combobox")).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /Register/i })).toBeInTheDocument();
    });

    test("submits form successfully", async () => {
        register.mockResolvedValueOnce({ message: "Registration successful" });

        renderWithRouter(<Register />);

        const usernameInput = screen.getByPlaceholderText(/Username/i);
        const emailInput = screen.getByPlaceholderText(/Email/i);
        const passwordInput = screen.getByPlaceholderText(/Password/i);
        const fullNameInput = screen.getByPlaceholderText(/Full Name/i);
        const dateOfBirthInput = screen.getByPlaceholderText(/Date of Birth/i);
        const roleSelect = screen.getByRole("combobox");
        const submitButton = screen.getByRole("button", { name: /Register/i });

        // Wait for roles to be populated
        await waitFor(() => expect(screen.getByText(/courier/i)).toBeInTheDocument());

        // Fill the form
        fireEvent.change(usernameInput, { target: { value: "testuser" } });
        fireEvent.change(emailInput, { target: { value: "test@example.com" } });
        fireEvent.change(passwordInput, { target: { value: "Password@123" } });
        fireEvent.change(fullNameInput, { target: { value: "Test User" } });
        fireEvent.change(dateOfBirthInput, { target: { value: "2000-01-01" } });
        fireEvent.change(roleSelect, { target: { value: "ROLE_COURIER" } });

        // Submit the form
        fireEvent.click(submitButton);

        // Assert the form submission
        await waitFor(() => {
            expect(register).toHaveBeenCalledWith({
                username: "testuser",
                email: "test@example.com",
                password: "Password@123",
                fullName: "Test User",
                dateOfBirth: "2000-01-01",
                role: "ROLE_COURIER",
            });
        });
    });
});
