import axios from "axios";

const API_URL = "/api/auth/";

const callApi = async (endpoint, data) => {
    const response = await axios.post(`${API_URL}${endpoint}`, data);
    return response.data;
};


export const login = async (usernameOrEmail, password) => {
    return callApi('login', {usernameOrEmail, password});
};

export const register = async (userData) => {
    return callApi('register', userData);
};