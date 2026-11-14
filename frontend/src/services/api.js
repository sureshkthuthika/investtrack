
import axios from "axios";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
    headers: {
        "Content-Type": "application/json"
    }
});

api.interceptors.request.use((config) => {
    const isAuthRequest = config.url?.startsWith("/auth/");

    if (!isAuthRequest) {
        const token = localStorage.getItem("token");

        if (token && token !== "undefined" && token !== "null") {
            config.headers.Authorization = `Bearer ${token}`;
        }
    }

    return config;
});

export default api;
