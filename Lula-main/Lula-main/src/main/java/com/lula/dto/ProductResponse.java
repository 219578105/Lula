package com.lula.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private LocalDate expiryDate;
    private String imageUrl;
    private String category;
    private boolean isActive;
    private Long businessId;
    private String businessName;
    private Integer daysUntilExpiry;
    private String status;
}
