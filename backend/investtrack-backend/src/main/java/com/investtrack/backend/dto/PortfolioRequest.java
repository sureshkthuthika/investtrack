package com.investtrack.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PortfolioRequest(

        @NotBlank(message = "Portfolio name is required")
        @Size(max = 100, message = "Portfolio name cannot exceed 100 characters")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description

) {}