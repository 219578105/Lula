package com.lula.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerRequest {
    @NotBlank(message = "Customer name is required")
    private String name;

    private String phone;
    private String email;
    private String address;
    private String notes;

    @PositiveOrZero(message = "Credit limit must be zero or positive")
    private BigDecimal creditLimit;
}
