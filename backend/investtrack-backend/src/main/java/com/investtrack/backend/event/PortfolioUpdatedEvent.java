package com.investtrack.backend.event;

public record PortfolioUpdatedEvent(
        String portfolioId,
        String eventType,
        String owner
) {
}
