
package com.investtrack.backend.repository;

import com.investtrack.backend.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository
        extends MongoRepository<Transaction, String> {

    List<Transaction> findByPortfolioIdAndOwnerOrderByTransactionDateDesc(
            String portfolioId,
            String owner
    );

    List<Transaction> findByPortfolioIdAndSymbolAndOwner(
            String portfolioId,
            String symbol,
            String owner
    );
}
