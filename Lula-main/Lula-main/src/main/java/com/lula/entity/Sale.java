package com.lula.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales", indexes = {
    @Index(name = "idx_sale_business", columnList = "business_id"),
    @Index(name = "idx_sale_customer", columnList = "customer_id"),
    @Index(name = "idx_sale_time", columnList = "sale_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale extends BaseEntity {

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "cash_received", precision = 10, scale = 2)
    private BigDecimal cashReceived;

    @Column(precision = 10, scale = 2)
    private BigDecimal changeAmount;

    @Column(name = "sale_time", nullable = false,columnDefinition = "TIMESTAMP")
    private LocalDateTime saleTime;

    @Column(name = "is_synced", nullable = false)
    private Boolean isSynced = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SaleItem> items = new ArrayList<>();
}
