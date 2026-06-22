package com.lula.entity;

import com.lula.enums.PaymentMethod;
import com.lula.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_pay_business", columnList = "business_id"),
    @Index(name = "idx_pay_customer", columnList = "customer_id"),
    @Index(name = "idx_pay_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING_CONFIRMATION;

    @Column(name = "paid_at", nullable = false,columnDefinition = "TIMESTAMP")
    private LocalDateTime paidAt;

    @Column(name = "confirmed_at",columnDefinition = "TIMESTAMP")
    private LocalDateTime confirmedAt;

    @Column(length = 500)
    private String proofImageUrl;

    @Column(length = 1000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}
