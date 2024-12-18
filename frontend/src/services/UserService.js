import axios from "axios";

const USER_BASE_URL = "http://localhost:8083/api/users";

export const createUser = async (createUserDTO) => {
    const response = await axios.post(`${USER_BASE_URL}`, createUserDTO, {
        headers: { "Content-Type": "application/json" },
    });
    return response.data;
};

// Get user by ID
export const getUserById = async (userId) => {
    const response = await axios.get(`${USER_BASE_URL}/${userId}`);
    return formatUserDates(response.data.data); // Format dates
};

// Helper function to format user dates
const formatUserDates = (user) => {
    return {
        ...user,
        dateOfBirth: convertDateArray(user.dateOfBirth),
    };
};

// Convert date array to JavaScript Date object
const convertDateArray = (dateArray) => {
    if (!Array.isArray(dateArray) || dateArray.length < 3) return "Invalid Date";
    const [year, month, day] = dateArray;
    return new Date(year, month - 1, day).toLocaleDateString(); // Return as readable string
};

export const updateUserProfile = async (id, updateData) => {
    const response = await axios.put(`${USER_BASE_URL}/${id}`, updateData, {
        headers: { "Content-Type": "application/json" },
    });
    return response.data;
};
