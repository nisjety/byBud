import axios from "axios";

const AUTH_BASE_URL = "http://localhost:8081/api/auth";

export const login = async (usernameOrEmail, password) => {
    const response = await axios.post(`${AUTH_BASE_URL}/login`, {
        usernameOrEmail,
        password,
    });
    return response.data;
};

export const register = async (registerData) => {
    const response = await axios.post(`${AUTH_BASE_URL}/register`, registerData, {
        headers: { "Content-Type": "application/json" },
    });
    return response.data;
};

export const getUserDetails = async (usernameOrEmail) => {
    const response = await axios.get(`${AUTH_BASE_URL}/user`, {
        params: { usernameOrEmail },
    });
    return response.data;
};
