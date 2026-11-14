
package com.investtrack.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionRequest(

        @NotBlank(message = "Symbol is required")
        @Size(max = 10, message = "Symbol cannot exceed 10 characters")
        @Pattern(regexp = "^[A-Za-z0-9.-]+$", message = "Invalid symbol format")
        String symbol,

        @NotBlank(message = "Transaction type is required")
        @Pattern(regexp = "^(BUY|SELL)$",
                flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "Transaction type must be BUY or SELL")
        String type,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.000001",
                message = "Quantity must be positive")
        BigDecimal quantity,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01",
                message = "Price must be positive")
        BigDecimal price,

        Instant transactionDate
) {}
