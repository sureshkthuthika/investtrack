import api from "./api";

const authService = {
    async register(userData) {
        const response = await api.post("/auth/register", userData);
        return response.data;
    },

    async login(credentials) {
        const response = await api.post("/auth/login", credentials);

        const { accessToken } = response.data;

        localStorage.setItem("token", accessToken);

        return response.data;
    },

    logout() {
        localStorage.removeItem("token");
    },

    isAuthenticated() {
        return Boolean(localStorage.getItem("token"));
    }
};

export default authService;