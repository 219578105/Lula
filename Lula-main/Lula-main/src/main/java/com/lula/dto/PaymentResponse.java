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
public class PaymentResponse {
    private Long id;
    private BigDecimal amount;
    private String method;
    private String status;
    private LocalDateTime paidAt;
    private LocalDateTime confirmedAt;
    private String notes;
    private String customerName;
    private String businessName;
}
