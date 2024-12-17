import axios from "axios";

// Base URLs for different services
const AUTH_BASE_URL = "http://localhost:8081/api";
const DELIVERY_BASE_URL = "http://localhost:8082/api";
const USER_BASE_URL = "http://localhost:8083/api";

// Mutex for preventing multiple concurrent refresh requests
let isRefreshing = false;
let refreshSubscribers = [];

// Subscribe to refresh token updates
const subscribeToTokenRefresh = (cb) => {
    refreshSubscribers.push(cb);
};

// Notify all subscribers when a new token is available
const onTokenRefreshed = (newToken) => {
    refreshSubscribers.forEach((cb) => cb(newToken));
    refreshSubscribers = [];
};

// Add Authorization header to requests
const addAuthInterceptor = (apiInstance) => {
    apiInstance.interceptors.request.use((config) => {
        const userData = JSON.parse(localStorage.getItem("userData"));
        const token = userData?.accessToken;

        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    });
};

// Handle token refresh
const addRefreshInterceptor = (apiInstance) => {
    apiInstance.interceptors.response.use(
        (response) => response,
        async (error) => {
            const originalRequest = error.config;

            // Handle 401 Unauthorized errors
            if (error.response?.status === 401 && localStorage.getItem("userData")) {
                const userData = JSON.parse(localStorage.getItem("userData"));
                const refreshToken = userData?.refreshToken;

                if (!refreshToken) {
                    localStorage.clear();
                    window.location.href = "/login";
                    return Promise.reject(error);
                }

                if (!isRefreshing) {
                    isRefreshing = true;

                    try {
                        const { data } = await axios.post(
                            `${AUTH_BASE_URL}/auth/refresh-token`,
                            { refreshToken },
                            { headers: { "Content-Type": "application/json" } }
                        );

                        // Update localStorage with new tokens
                        const updatedUserData = {
                            ...userData,
                            accessToken: data.accessToken,
                            refreshToken: data.refreshToken,
                        };
                        localStorage.setItem("userData", JSON.stringify(updatedUserData));

                        isRefreshing = false;
                        onTokenRefreshed(data.accessToken);
                    } catch (refreshError) {
                        isRefreshing = false;
                        localStorage.clear();
                        window.location.href = "/login";
                        return Promise.reject(refreshError);
                    }
                }

                // Queue failed requests and retry them after token refresh
                return new Promise((resolve) => {
                    subscribeToTokenRefresh((newToken) => {
                        originalRequest.headers.Authorization = `Bearer ${newToken}`;
                        resolve(axios(originalRequest));
                    });
                });
            }

            return Promise.reject(error);
        }
    );
};

// Add error handler
const addErrorHandler = (apiInstance) => {
    apiInstance.interceptors.response.use(
        (response) => response,
        (error) => {
            if (error.response) {
                console.error("API Error:", error.response.status, error.response.data);
            } else {
                console.error("Network Error:", error.message);
            }
            return Promise.reject(error);
        }
    );
};

// Create API with shared interceptors
const createAPI = (baseURL) => {
    const apiInstance = axios.create({ baseURL });
    addAuthInterceptor(apiInstance);
    addRefreshInterceptor(apiInstance);
    addErrorHandler(apiInstance);
    return apiInstance;
};

// APIs for different services
const AuthAPI = createAPI(AUTH_BASE_URL);
const DeliveryAPI = createAPI(DELIVERY_BASE_URL);
const UserAPI = createAPI(USER_BASE_URL);

export { AuthAPI, DeliveryAPI, UserAPI };
