import React from "react";
import { render, screen, fireEvent, waitFor, within } from "@testing-library/react";
import { getUserProfile, updateUserProfile } from "../services/UserService";
import UserProfile from "../pages/UserProfile";

jest.mock("../services/UserService", () => ({
    getUserProfile: jest.fn(),
    updateUserProfile: jest.fn(),
}));

describe("UserProfile Component", () => {
    const mockProfile = {
        username: "testuser",
        fullName: "Test User",
        email: "testuser@example.com",
        dateOfBirth: "1990-01-01",
        roles: ["ROLE_USER", "ROLE_ADMIN"],
    };

    beforeEach(() => {
        localStorage.setItem("userId", "user123"); // Mock localStorage userId
    });

    afterEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
    });

    test("displays user profile correctly after fetching", async () => {
        getUserProfile.mockResolvedValueOnce(mockProfile);

        render(<UserProfile />);

        // Check loading state
        expect(screen.getByText(/Loading profile/i)).toBeInTheDocument();

        // Wait for the profile to load and scope queries using `within`
        await waitFor(() => {
            const profileRegion = screen.getByRole("region", { name: /Your Profile/i });
            const withinProfile = within(profileRegion);

            // Verify each field individually within the scoped region
            expect(withinProfile.getByText("testuser")).toBeInTheDocument(); // Username
            expect(withinProfile.getByText("Test User")).toBeInTheDocument(); // Full Name
            expect(withinProfile.getByText("testuser@example.com")).toBeInTheDocument(); // Email
            expect(withinProfile.getByText("1990-01-01")).toBeInTheDocument(); // Date of Birth
            expect(withinProfile.getByText("ROLE_USER, ROLE_ADMIN")).toBeInTheDocument(); // Roles
        });
    });


    test("allows editing and saving changes to the user profile", async () => {
        getUserProfile.mockResolvedValueOnce(mockProfile);
        updateUserProfile.mockResolvedValueOnce({
            ...mockProfile,
            fullName: "Updated User",
        });

        render(<UserProfile />);

        // Wait for the profile to load
        await waitFor(() => {
            expect(screen.getByRole("region", { name: /Your Profile/i })).toBeInTheDocument();
        });

        // Click the edit button
        fireEvent.click(screen.getByText(/Edit Profile/i));

        // Update the full name
        fireEvent.change(screen.getByLabelText(/Full Name/i), {
            target: { value: "Updated User" },
        });

        // Save the changes
        fireEvent.click(screen.getByText(/Save Changes/i));

        // Verify the update API was called with the correct data
        await waitFor(() => {
            expect(updateUserProfile).toHaveBeenCalledWith("user123", {
                ...mockProfile,
                fullName: "Updated User",
            });
        });

        // Check that the updated profile is displayed
        await waitFor(() => {
            expect(screen.getByText(/Updated User/i)).toBeInTheDocument();
        });
    });

    test("displays an error message if fetching the profile fails", async () => {
        getUserProfile.mockRejectedValueOnce(new Error("Failed to fetch profile"));

        render(<UserProfile />);

        // Check loading state
        expect(screen.getByText(/Loading profile/i)).toBeInTheDocument();

        // Wait for the error message
        await waitFor(() => {
            expect(screen.getByRole("alert")).toHaveTextContent(/Failed to fetch profile/i);
        });
    });

    test("displays an error message if updating the profile fails", async () => {
        getUserProfile.mockResolvedValueOnce(mockProfile);
        updateUserProfile.mockRejectedValueOnce(new Error("Failed to update profile"));

        render(<UserProfile />);

        // Wait for the profile to load
        await waitFor(() => {
            expect(screen.getByRole("region", { name: /Your Profile/i })).toBeInTheDocument();
        });

        // Click the edit button
        fireEvent.click(screen.getByText(/Edit Profile/i));

        // Update the full name
        fireEvent.change(screen.getByLabelText(/Full Name/i), {
            target: { value: "Updated User" },
        });

        // Save the changes
        fireEvent.click(screen.getByText(/Save Changes/i));

        // Check that the error message is displayed
        await waitFor(() => {
            expect(screen.getByRole("alert")).toHaveTextContent(/Failed to update profile/i);
        });
    });
});
