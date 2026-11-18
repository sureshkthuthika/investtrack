
import api from "./api";

const portfolioService = {
    async getPortfolios() {
        const response = await api.get("/portfolios");
        return response.data;
    },

    async getPortfolioSummary(portfolioId) {
        const response = await api.get(`/portfolios/${portfolioId}/summary`);
        return response.data;
    },

    async createPortfolio(portfolioData) {
        const response = await api.post("/portfolios", portfolioData);
        return response.data;
    },

    async updatePortfolio(portfolioId, portfolioData) {
        const response = await api.put(
            `/portfolios/${portfolioId}`,
            portfolioData
        );
        return response.data;
    },

    async deletePortfolio(portfolioId) {
        const response = await api.delete(
            `/portfolios/${portfolioId}`
        );
        return response.data;
    },

    async getHoldings(portfolioId) {
        const response = await api.get(
            `/portfolios/${portfolioId}/holdings`
        );
        return response.data;
    },

    async getTransactions(portfolioId) {
        const response = await api.get(
            `/portfolios/${portfolioId}/transactions`
        );
        return response.data;
    },

    async addTransaction(portfolioId, transactionData) {
        const response = await api.post(
            `/portfolios/${portfolioId}/transactions`,
            transactionData
        );
        return response.data;
    },

    async addHolding(portfolioId, holdingData) {
        const response = await api.post(
            `/portfolios/${portfolioId}/holdings`,
            holdingData
        );
        return response.data;
    },

    async updateHolding(portfolioId, holdingId, holdingData) {
        const response = await api.put(
            `/portfolios/${portfolioId}/holdings/${holdingId}`,
            holdingData
        );
        return response.data;
    },

    async deleteHolding(portfolioId, holdingId) {
        await api.delete(
            `/portfolios/${portfolioId}/holdings/${holdingId}`
        );
    },
};

export default portfolioService;
