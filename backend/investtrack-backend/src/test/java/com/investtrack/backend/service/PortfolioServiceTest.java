
package com.investtrack.backend.service;

import com.investtrack.backend.entity.Portfolio;
import com.investtrack.backend.exception.ResourceNotFoundException;
import com.investtrack.backend.repository.PortfolioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    void getAllPortfolios_returnsOnlyCurrentUserPortfolios() {
        String owner = "investtrack.demo@example.com";

        List<Portfolio> portfolios = List.of(
                new Portfolio("My Portfolio", owner, "Demo portfolio")
        );

        when(portfolioRepository.findByOwner(owner))
                .thenReturn(portfolios);

        List<Portfolio> result =
                portfolioService.getAllPortfolios(owner);

        assertEquals(1, result.size());
        assertEquals(owner, result.get(0).getOwner());

        verify(portfolioRepository).findByOwner(owner);
    }

    @Test
    void getPortfolioById_whenPortfolioBelongsToAnotherUser_throwsNotFound() {
        String portfolioId = "portfolio123";
        String currentUser = "investtrack.second@example.com";

        when(portfolioRepository.findByIdAndOwner(
                portfolioId, currentUser))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> portfolioService.getPortfolioById(
                        portfolioId, currentUser)
        );

        verify(portfolioRepository)
                .findByIdAndOwner(portfolioId, currentUser);
    }
}
