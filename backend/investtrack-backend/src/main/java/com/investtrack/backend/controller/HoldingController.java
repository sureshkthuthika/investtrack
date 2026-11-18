package com.investtrack.backend.controller;

import com.investtrack.backend.dto.HoldingRequest;
import com.investtrack.backend.entity.Holding;
import com.investtrack.backend.service.HoldingService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/holdings")
public class HoldingController {

    private final HoldingService holdingService;

    public HoldingController(HoldingService holdingService) {
        this.holdingService = holdingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Holding addHolding(
            @PathVariable String portfolioId,
            @Valid @RequestBody HoldingRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return holdingService.addHolding(
                portfolioId, request, jwt.getSubject());
    }

    @GetMapping
    public List<Holding> getHoldings(
            @PathVariable String portfolioId,
            @AuthenticationPrincipal Jwt jwt) {

        return holdingService.getHoldings(
                portfolioId, jwt.getSubject());
    }

    @PutMapping("/{holdingId}")
    public Holding updateHolding(
            @PathVariable String portfolioId,
            @PathVariable String holdingId,
            @Valid @RequestBody HoldingRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return holdingService.updateHolding(
                portfolioId,
                holdingId,
                request,
                jwt.getSubject()
        );
    }

    @DeleteMapping("/{holdingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHolding(
            @PathVariable String portfolioId,
            @PathVariable String holdingId,
            @AuthenticationPrincipal Jwt jwt) {

        holdingService.deleteHolding(
                portfolioId, holdingId, jwt.getSubject());
    }
}