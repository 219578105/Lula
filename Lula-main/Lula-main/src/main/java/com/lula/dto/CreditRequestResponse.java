package com.lula.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequestResponse {
    private Long id;
    private String item;
    private BigDecimal amount;
    private String note;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;
    private String responseNote;
    private String customerName;
    private String customerEmail;
    private String businessName;
    private Long businessId;
}
