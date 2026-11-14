package com.investtrack.backend.service;

import com.investtrack.backend.dto.PortfolioRequest;
import com.investtrack.backend.entity.Portfolio;
import com.investtrack.backend.exception.ResourceNotFoundException;
import com.investtrack.backend.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import com.investtrack.backend.dto.PortfolioSummaryResponse;
import com.investtrack.backend.entity.Holding;
import com.investtrack.backend.repository.HoldingRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;

    public PortfolioService(PortfolioRepository portfolioRepository,
                            HoldingRepository holdingRepository) {
        this.portfolioRepository = portfolioRepository;
        this.holdingRepository = holdingRepository;
    }

    // Create portfolio for authenticated user
    public Portfolio createPortfolio(PortfolioRequest request, String owner) {

        Portfolio portfolio = new Portfolio();

        portfolio.setName(request.name());
        portfolio.setOwner(owner);
        portfolio.setDescription(request.description());
        portfolio.setCreatedAt(Instant.now());

        return portfolioRepository.save(portfolio);
    }

    // Get only the authenticated user's portfolios
    public List<Portfolio> getAllPortfolios(String owner) {
        return portfolioRepository.findByOwner(owner);
    }

    // Find portfolio by ID and owner
    public Portfolio getPortfolioById(String id, String owner) {
        return portfolioRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Portfolio not found with id: " + id
                ));
    }

    // Delete only the authenticated user's portfolio
    public void deletePortfolio(String id, String owner) {
        Portfolio portfolio = getPortfolioById(id, owner);
        portfolioRepository.delete(portfolio);
    }

    // Update portfolio without changing its owner
    public Portfolio updatePortfolio(
            String id, PortfolioRequest request, String owner) {

        Portfolio existingPortfolio = getPortfolioById(id, owner);

        existingPortfolio.setName(request.name());
        existingPortfolio.setDescription(request.description());

        return portfolioRepository.save(existingPortfolio);
    }

    // Partial update
    public Portfolio patchPortfolio(
            String id, Map<String, Object> updates, String owner) {

        Portfolio existingPortfolio = getPortfolioById(id, owner);

        if (updates == null || updates.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one field is required");
        }

        for (String field : updates.keySet()) {
            if (!List.of("name", "description").contains(field)) {
                throw new IllegalArgumentException(
                        "Unsupported field: " + field);
            }
        }

        if (updates.containsKey("name")) {
            existingPortfolio.setName(
                    validateString(updates.get("name"), "name", 100, true)
            );
        }

        if (updates.containsKey("description")) {
            existingPortfolio.setDescription(
                    validateString(updates.get("description"),
                            "description", 500, false)
            );
        }

        return portfolioRepository.save(existingPortfolio);
    }

    private String validateString(
            Object value, String field, int maxLength, boolean required) {

        if (!(value instanceof String text)) {
            throw new IllegalArgumentException(
                    field + " must be a string");
        }

        if (required && text.isBlank()) {
            throw new IllegalArgumentException(
                    field + " cannot be blank");
        }

        if (text.length() > maxLength) {
            throw new IllegalArgumentException(
                    field + " cannot exceed " + maxLength + " characters");
        }

        return text;
    }

    public PortfolioSummaryResponse getPortfolioSummary(String portfolioId,
                                                        String owner) {

        portfolioRepository.findByIdAndOwner(portfolioId, owner)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio not found"));

        List<Holding> holdings =
                holdingRepository.findByPortfolioIdAndOwner(portfolioId, owner);

        BigDecimal totalShares = BigDecimal.ZERO;
        BigDecimal totalCostBasis = BigDecimal.ZERO;

        List<PortfolioSummaryResponse.HoldingSummary> holdingSummaries =
                new ArrayList<>();

        for (Holding holding : holdings) {

            BigDecimal quantity = holding.getQuantity();
            BigDecimal averageCost = holding.getAverageCost();

            BigDecimal costBasis = quantity.multiply(averageCost);

            totalShares = totalShares.add(quantity);
            totalCostBasis = totalCostBasis.add(costBasis);

            holdingSummaries.add(
                    new PortfolioSummaryResponse.HoldingSummary(
                            holding.getSymbol(),
                            quantity,
                            averageCost,
                            costBasis
                    )
            );
        }

        return new PortfolioSummaryResponse(
                portfolioId,
                holdings.size(),
                totalShares,
                totalCostBasis,
                holdingSummaries
        );
    }
}