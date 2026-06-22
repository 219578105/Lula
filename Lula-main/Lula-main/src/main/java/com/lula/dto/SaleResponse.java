package com.lula.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse {
    private Long id;
    private String description;
    private BigDecimal total;
    private BigDecimal cashReceived;
    private BigDecimal changeAmount;
    private LocalDateTime saleTime;
    private boolean isSynced;
    private String customerName;
    private Long businessId;
    private String businessName;
    private List<SaleItemResponse> items;
}
