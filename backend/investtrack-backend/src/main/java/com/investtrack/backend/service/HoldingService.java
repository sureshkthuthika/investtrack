package com.investtrack.backend.service;

import com.investtrack.backend.dto.HoldingRequest;
import com.investtrack.backend.entity.Holding;
import com.investtrack.backend.exception.ResourceNotFoundException;
import com.investtrack.backend.repository.HoldingRepository;
import com.investtrack.backend.repository.PortfolioRepository;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class HoldingService {

    private final HoldingRepository holdingRepository;
    private final PortfolioRepository portfolioRepository;

    public HoldingService(
            HoldingRepository holdingRepository,
            PortfolioRepository portfolioRepository) {
        this.holdingRepository = holdingRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public Holding addHolding(
            String portfolioId,
            HoldingRequest request,
            String owner) {

        // Verify portfolio belongs to the logged-in user
        portfolioRepository.findByIdAndOwner(portfolioId, owner)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Portfolio not found"));

        Holding holding = new Holding();
        holding.setPortfolioId(portfolioId);
        holding.setOwner(owner);
        holding.setSymbol(request.symbol().trim().toUpperCase());
        holding.setQuantity(request.quantity());
        holding.setAverageCost(request.averageCost());
        holding.setCreatedAt(Instant.now());

        return holdingRepository.save(holding);
    }

    public List<Holding> getHoldings(
            String portfolioId,
            String owner) {

        // Verify portfolio ownership
        portfolioRepository.findByIdAndOwner(portfolioId, owner)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Portfolio not found"));

        return holdingRepository.findByPortfolioIdAndOwner(
                portfolioId, owner);
    }

    public void deleteHolding(
            String portfolioId,
            String holdingId,
            String owner) {

        // Verify portfolio ownership
        portfolioRepository.findByIdAndOwner(portfolioId, owner)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Portfolio not found"));

        Holding holding = holdingRepository
                .findByIdAndPortfolioIdAndOwner(
                        holdingId, portfolioId, owner)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Holding not found"));

        holdingRepository.delete(holding);
    }
}