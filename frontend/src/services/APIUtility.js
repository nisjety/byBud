import axios from "axios";
const AUTH_BASE_URL = "http://localhost:8081/api";
const DELIVERY_BASE_URL = "http://localhost:8082/api";
const USER_BASE_URL = "http://localhost:8083/api";

// Create an Axios instance with shared configuration
const createAPI = (baseURL) => {
    const apiInstance = axios.create({ baseURL });
    apiInstance.interceptors.response.use(
        (response) => response,
        (error) => {
            console.error("API Error:", error.message);
            return Promise.reject(error);
        }
    );
    return apiInstance;
};

// APIs for different services
const AuthAPI = createAPI(AUTH_BASE_URL);
const DeliveryAPI = createAPI(DELIVERY_BASE_URL);
const UserAPI = createAPI(USER_BASE_URL);

export { AuthAPI, DeliveryAPI, UserAPI };
