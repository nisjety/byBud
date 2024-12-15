import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import Login from "../pages/Login";
import { login } from "../services/AuthService";

jest.mock("../services/AuthService", () => ({
    login: jest.fn(),
}));

describe("Login Component", () => {
    beforeEach(() => {
        localStorage.clear(); // Clear localStorage before each test
        jest.clearAllMocks(); // Clear mocks
    });

    test("renders Login component successfully", () => {
        render(<Login />);

        expect(screen.getByRole("heading", { name: /Login/i })).toBeInTheDocument();
        expect(screen.getByPlaceholderText(/Username or Email/i)).toBeInTheDocument();
        expect(screen.getByPlaceholderText(/Password/i)).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /Login/i })).toBeInTheDocument();
    });

    test("handles successful login", async () => {
        const mockResponse = {
            accessToken: "mockAccessToken",
            refreshToken: "mockRefreshToken",
            roles: ["ROLE_USER"],
        };

        login.mockResolvedValueOnce(mockResponse);

        render(<Login />);

        const usernameInput = screen.getByPlaceholderText(/Username or Email/i);
        const passwordInput = screen.getByPlaceholderText(/Password/i);
        const loginButton = screen.getByRole("button", { name: /Login/i });

        fireEvent.change(usernameInput, { target: { value: "testuser" } });
        fireEvent.change(passwordInput, { target: { value: "password123" } });
        fireEvent.click(loginButton);

        await waitFor(() => {
            expect(login).toHaveBeenCalledWith("testuser", "password123");
            expect(localStorage.getItem("token")).toBe(mockResponse.accessToken);
            expect(localStorage.getItem("refreshToken")).toBe(mockResponse.refreshToken);
            expect(localStorage.getItem("roles")).toBe(JSON.stringify(mockResponse.roles));
        });
    });

    test("shows error message on failed login", async () => {
        login.mockRejectedValueOnce({
            response: { data: { message: "Invalid credentials" } },
        });

        render(<Login />);

        const usernameInput = screen.getByPlaceholderText(/Username or Email/i);
        const passwordInput = screen.getByPlaceholderText(/Password/i);
        const loginButton = screen.getByRole("button", { name: /Login/i });

        fireEvent.change(usernameInput, { target: { value: "testuser" } });
        fireEvent.change(passwordInput, { target: { value: "wrongpassword" } });
        fireEvent.click(loginButton);

        await waitFor(() => {
            expect(login).toHaveBeenCalledWith("testuser", "wrongpassword");
            expect(screen.getByText(/Invalid credentials/i)).toBeInTheDocument();
        });
    });

    test("shows fallback error message if no response message", async () => {
        login.mockRejectedValueOnce(new Error("Login failed"));

        render(<Login />);

        const usernameInput = screen.getByPlaceholderText(/Username or Email/i);
        const passwordInput = screen.getByPlaceholderText(/Password/i);
        const loginButton = screen.getByRole("button", { name: /Login/i });

        fireEvent.change(usernameInput, { target: { value: "testuser" } });
        fireEvent.change(passwordInput, { target: { value: "password123" } });
        fireEvent.click(loginButton);

        await waitFor(() => {
            expect(screen.getByText(/Login failed/i)).toBeInTheDocument();
        });
    });
});
