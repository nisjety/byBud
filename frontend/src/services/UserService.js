import API from "./APIUtility";

const USER_API_URL = "/users/";

// Fetch user profile by ID (matches UserDTO)
export const getUserProfile = async (userId) => {
    const response = await API.get(`${USER_API_URL}${userId}`);
    return response.data; // Returns a UserDTO
};

// Update user profile (matches UserDTO fields)
export const updateUserProfile = async (userId, updatedData) => {
    const response = await API.put(`${USER_API_URL}${userId}`, updatedData);
    return response.data; // Updated UserDTO is returned
};

// Fetch users by role (returns List<UserDTO>)
export const getUsersByRole = async (role) => {
    const response = await API.get(`${USER_API_URL}role/${role}`);
    return response.data;
};
