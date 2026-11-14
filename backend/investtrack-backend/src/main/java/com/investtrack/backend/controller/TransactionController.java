
package com.investtrack.backend.controller;

import com.investtrack.backend.dto.TransactionRequest;
import com.investtrack.backend.entity.Transaction;
import com.investtrack.backend.service.TransactionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction recordTransaction(
            @PathVariable String portfolioId,
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return transactionService.recordTransaction(
                portfolioId, request, jwt.getSubject());
    }

    @GetMapping
    public List<Transaction> getTransactions(
            @PathVariable String portfolioId,
            @AuthenticationPrincipal Jwt jwt) {

        return transactionService.getTransactions(
                portfolioId, jwt.getSubject());
    }
}
