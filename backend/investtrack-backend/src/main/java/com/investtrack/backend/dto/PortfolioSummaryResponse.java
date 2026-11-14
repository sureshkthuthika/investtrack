
package com.investtrack.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioSummaryResponse(
        String portfolioId,
        int holdingsCount,
        BigDecimal totalShares,
        BigDecimal totalCostBasis,
        List<HoldingSummary> holdings
) {
    public record HoldingSummary(
            String symbol,
            BigDecimal quantity,
            BigDecimal averageCost,
            BigDecimal costBasis
    ) {}
}
