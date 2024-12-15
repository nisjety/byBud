import axios from "axios";

const API = axios.create({
    baseURL: "/api", // Centralized base URL for all API endpoints
});

// Add a request interceptor to include the Authorization header
API.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// Add a response interceptor to handle errors globally
API.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem("token");
            window.location.href = "/login"; // Redirect to login if unauthorized
        }
        return Promise.reject(error);
    }
);

export default API;
