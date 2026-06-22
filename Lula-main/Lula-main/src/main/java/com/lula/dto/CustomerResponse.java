package com.lula.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private BigDecimal creditLimit;
    private BigDecimal balance;
    private String address;
    private String notes;
    private Long businessId;
    private String businessName;
    private boolean overLimit;
}
