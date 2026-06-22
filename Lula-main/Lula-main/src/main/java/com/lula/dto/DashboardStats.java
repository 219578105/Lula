package com.lula.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private BigDecimal todayRevenue;
    private Integer todayTransactions;
    private Integer totalProducts;
    private Integer totalCustomers;
    private Integer pendingSync;
    private Integer lowStockCount;
    private Integer expiringCount;
    private Integer creditRequestsCount;
    private List<AlertDto> alerts;
}
