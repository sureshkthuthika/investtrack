package com.investtrack.backend.repository;

import com.investtrack.backend.entity.Holding;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface HoldingRepository extends MongoRepository<Holding, String> {

    List<Holding> findByPortfolioIdAndOwner(
            String portfolioId, String owner);

    Optional<Holding> findByIdAndPortfolioIdAndOwner(
            String id, String portfolioId, String owner);

    void deleteByIdAndPortfolioIdAndOwner(
            String id, String portfolioId, String owner);

    Optional<Holding> findByPortfolioIdAndSymbolAndOwner(
            String portfolioId,
            String symbol,
            String owner
    );
}