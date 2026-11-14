
package com.investtrack.backend.service;

import com.investtrack.backend.dto.TransactionRequest;
import com.investtrack.backend.entity.Holding;
import com.investtrack.backend.entity.Transaction;
import com.investtrack.backend.exception.ResourceNotFoundException;
import com.investtrack.backend.repository.HoldingRepository;
import com.investtrack.backend.repository.PortfolioRepository;
import com.investtrack.backend.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final HoldingRepository holdingRepository;
    private final PortfolioRepository portfolioRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            HoldingRepository holdingRepository,
            PortfolioRepository portfolioRepository) {
        this.transactionRepository = transactionRepository;
        this.holdingRepository = holdingRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Transactional
    public Transaction recordTransaction(
            String portfolioId,
            TransactionRequest request,
            String owner) {

        portfolioRepository.findByIdAndOwner(portfolioId, owner)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio not found"));

        String symbol = request.symbol().trim().toUpperCase();
        String type = request.type().trim().toUpperCase();

        BigDecimal quantity = request.quantity();
        BigDecimal price = request.price();
        BigDecimal totalAmount = quantity.multiply(price);

        Holding holding = holdingRepository
                .findByPortfolioIdAndSymbolAndOwner(
                        portfolioId, symbol, owner)
                .orElse(null);

        if ("BUY".equals(type)) {

            if (holding == null) {
                holding = new Holding();
                holding.setPortfolioId(portfolioId);
                holding.setOwner(owner);
                holding.setSymbol(symbol);
                holding.setQuantity(quantity);
                holding.setAverageCost(price);
                holding.setCreatedAt(Instant.now());
            } else {
                BigDecimal oldQuantity = holding.getQuantity();
                BigDecimal newQuantity = oldQuantity.add(quantity);

                BigDecimal oldCost = oldQuantity.multiply(
                        holding.getAverageCost());

                BigDecimal newCost = quantity.multiply(price);

                BigDecimal averageCost = oldCost.add(newCost)
                        .divide(newQuantity, 4, RoundingMode.HALF_UP);

                holding.setQuantity(newQuantity);
                holding.setAverageCost(averageCost);
            }

            holdingRepository.save(holding);

        } else if ("SELL".equals(type)) {

            if (holding == null ||
                    holding.getQuantity().compareTo(quantity) < 0) {
                throw new IllegalArgumentException(
                        "Insufficient holding quantity to sell");
            }

            BigDecimal remainingQuantity =
                    holding.getQuantity().subtract(quantity);

            if (remainingQuantity.compareTo(BigDecimal.ZERO) == 0) {
                holdingRepository.delete(holding);
            } else {
                holding.setQuantity(remainingQuantity);
                holdingRepository.save(holding);
            }
        }

        Transaction transaction = new Transaction();
        transaction.setPortfolioId(portfolioId);
        transaction.setOwner(owner);
        transaction.setSymbol(symbol);
        transaction.setType(type);
        transaction.setQuantity(quantity);
        transaction.setPrice(price);
        transaction.setTotalAmount(totalAmount);
        transaction.setTransactionDate(
                request.transactionDate() != null
                        ? request.transactionDate()
                        : Instant.now());
        transaction.setCreatedAt(Instant.now());

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactions(
            String portfolioId, String owner) {

        portfolioRepository.findByIdAndOwner(portfolioId, owner)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Portfolio not found"));

        return transactionRepository
                .findByPortfolioIdAndOwnerOrderByTransactionDateDesc(
                        portfolioId, owner);
    }
}
