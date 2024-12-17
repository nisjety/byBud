import { AuthAPI } from "./APIUtility";

const AUTH_API_URL = "/auth";

export const login = async (usernameOrEmail, password) => {
    const formData = new FormData();
    formData.append("usernameOrEmail", usernameOrEmail);
    formData.append("password", password);

    const response = await AuthAPI.post(`${AUTH_API_URL}/login`, formData, {
        headers: { "Content-Type": "multipart/form-data" }, // Ensure form-data is used
    });

    return response.data; // Response matches the backend structure
};



export const register = async (registerData) => {
    const response = await AuthAPI.post(`${AUTH_API_URL}/register`, registerData, {
        headers: { "Content-Type": "application/json" },
    });
    return response.data;
};

export const refreshToken = async (refreshToken) => {
    const response = await AuthAPI.post(
        `${AUTH_API_URL}/refresh-token`,
        { refreshToken },
        { headers: { "Content-Type": "application/json" } }
    );
    return response.data;
};
