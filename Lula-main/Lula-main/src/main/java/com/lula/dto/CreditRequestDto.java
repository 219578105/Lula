package com.lula.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditRequestDto {
    @NotNull(message = "Business ID is required")
    private Long businessId;

    @NotBlank(message = "Item description is required")
    private String item;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String note;
}
