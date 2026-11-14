package com.investtrack.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record HoldingRequest(

        @NotBlank(message = "Symbol is required")
        @Size(max = 10, message = "Symbol cannot exceed 10 characters")
        @Pattern(
                regexp = "^[A-Za-z0-9.-]+$",
                message = "Invalid symbol format"
        )
        String symbol,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.000001", message = "Quantity must be positive")
        BigDecimal quantity,

        @NotNull(message = "Average cost is required")
        @DecimalMin(value = "0.01", message = "Average cost must be positive")
        BigDecimal averageCost
) {}