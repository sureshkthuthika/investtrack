package com.investtrack.backend.service;

import com.investtrack.backend.dto.HoldingRequest;
import com.investtrack.backend.entity.Holding;
import com.investtrack.backend.entity.Portfolio;
import com.investtrack.backend.repository.HoldingRepository;
import com.investtrack.backend.repository.PortfolioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HoldingServiceTest {

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private HoldingService holdingService;

    @Test
    void addHolding_shouldSaveHoldingForOwner() {

        String portfolioId = "portfolio123";
        String owner = "investtrack.demo@example.com";

        Portfolio portfolio = new Portfolio(
                "Retirement Portfolio", owner, "Long-term investment");

        when(portfolioRepository.findByIdAndOwner(portfolioId, owner))
                .thenReturn(Optional.of(portfolio));

        HoldingRequest request = new HoldingRequest(
                "aapl",
                new BigDecimal("10"),
                new BigDecimal("225.50"));

        Holding savedHolding = new Holding();

        when(holdingRepository.save(any(Holding.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Holding result = holdingService.addHolding(
                portfolioId, request, owner);

        assertEquals(portfolioId, result.getPortfolioId());
        assertEquals(owner, result.getOwner());
        assertEquals("AAPL", result.getSymbol());
        assertEquals(new BigDecimal("10"), result.getQuantity());
        assertEquals(new BigDecimal("225.50"), result.getAverageCost());

        verify(holdingRepository).save(any(Holding.class));
    }
}