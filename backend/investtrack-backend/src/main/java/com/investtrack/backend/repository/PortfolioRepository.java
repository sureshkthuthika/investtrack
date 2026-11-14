package com.investtrack.backend.repository;

import com.investtrack.backend.entity.Portfolio;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository
        extends MongoRepository<Portfolio, String> {

    List<Portfolio> findByOwner(String owner);

    Optional<Portfolio> findByIdAndOwner(String id, String owner);
}