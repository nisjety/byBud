import API from "./APIUtility";

const AUTH_API_URL = "/auth/";

export const login = async (usernameOrEmail, password) => {
    const response = await API.post(`${AUTH_API_URL}login`, { usernameOrEmail, password });
    const { accessToken, refreshToken, roles } = response.data; // Extract DTO fields
    return { accessToken, refreshToken, roles };
};

export const register = async (registerData) => {
    const response = await API.post(`${AUTH_API_URL}register`, registerData);
    return response.data; // No changes as the DTO structure matches the backend
};

export const refreshToken = async (refreshToken) => {
    const response = await API.post(`${AUTH_API_URL}refresh-token`, { refreshToken });
    return response.data; // Handle new tokens in the response
};
