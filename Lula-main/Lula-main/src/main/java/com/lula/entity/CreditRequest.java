package com.lula.entity;

import com.lula.enums.CreditStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_requests", indexes = {
    @Index(name = "idx_cr_business", columnList = "business_id"),
    @Index(name = "idx_cr_customer", columnList = "customer_id"),
    @Index(name = "idx_cr_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditRequest extends BaseEntity {

    @Column(nullable = false, length = 500)
    private String item;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 1000)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CreditStatus status = CreditStatus.PENDING;

    @Column(name = "requested_at", nullable = false,columnDefinition = "TIMESTAMP")
    private LocalDateTime requestedAt;

    @Column(name = "responded_at",columnDefinition = "TIMESTAMP")
    private LocalDateTime respondedAt;

    @Column(length = 500)
    private String responseNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}
