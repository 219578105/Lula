package com.lula.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleRequest {
    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Total is required")
    @PositiveOrZero(message = "Total must be zero or positive")
    private BigDecimal total;

    @PositiveOrZero(message = "Cash received must be zero or positive")
    private BigDecimal cashReceived;

    private BigDecimal changeAmount;
    private Long customerId;
    private List<SaleItemRequest> items;
}
