
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
    }
};

export default portfolioService;
