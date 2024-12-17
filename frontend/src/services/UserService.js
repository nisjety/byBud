import { UserAPI } from "./APIUtility";

const USER_API_URL = "/users/";
const ROLE_API_URL = "/roles";

export const getUserProfile = async (userId) => {
    const response = await UserAPI.get(`${USER_API_URL}${userId}`);
    return response.data; // Returns UserDTO
};

export const updateUserProfile = async (userId, updatedData) => {
    const response = await UserAPI.put(`${USER_API_URL}${userId}`, updatedData);
    return response.data; // Updated UserDTO
};

export const getRoles = async () => {
    const response = await UserAPI.get(ROLE_API_URL);
    console.log("Roles response:", response.data);
    if (Array.isArray(response.data) && response.data.every((role) => role.name)) {
        return response.data.map((role) => role.name); // Extract role names (e.g., ROLE_ADMIN)
    }
    throw new Error("Unexpected roles data format");
};