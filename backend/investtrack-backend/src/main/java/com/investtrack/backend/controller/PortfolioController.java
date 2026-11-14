package com.investtrack.backend.controller;

import com.investtrack.backend.dto.PortfolioRequest;
import com.investtrack.backend.entity.Portfolio;
import com.investtrack.backend.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.investtrack.backend.dto.PortfolioSummaryResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Portfolio createPortfolio(
            @Valid @RequestBody PortfolioRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return portfolioService.createPortfolio(
                request, jwt.getSubject());
    }

    @GetMapping
    public ResponseEntity<List<Portfolio>> getAllPortfolios(
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(
                portfolioService.getAllPortfolios(jwt.getSubject()));
    }

    @GetMapping("/{id}")
    public Portfolio getPortfolioById(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

        return portfolioService.getPortfolioById(
                id, jwt.getSubject());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortfolio(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

        portfolioService.deletePortfolio(id, jwt.getSubject());
    }

    @PutMapping("/{id}")
    public Portfolio updatePortfolio(
            @PathVariable String id,
            @Valid @RequestBody PortfolioRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return portfolioService.updatePortfolio(
                id, request, jwt.getSubject());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Portfolio> patchPortfolio(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(
                portfolioService.patchPortfolio(
                        id, updates, jwt.getSubject()));
    }

    @GetMapping("/{id}/summary")
    public PortfolioSummaryResponse getPortfolioSummary(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

        return portfolioService.getPortfolioSummary(
                id,
                jwt.getSubject()
        );
    }
}