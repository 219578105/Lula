package com.lula.repository;

import com.lula.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByBusinessIdOrderBySaleTimeDesc(Long businessId);

    @Query("SELECT s FROM Sale s WHERE s.business.id = :businessId AND s.saleTime >= :start AND s.saleTime < :end ORDER BY s.saleTime DESC")
    List<Sale> findTodaySales(@Param("businessId") Long businessId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s WHERE s.business.id = :businessId AND s.saleTime >= :start AND s.saleTime < :end")
    java.math.BigDecimal sumTodayRevenue(@Param("businessId") Long businessId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Sale> findByCustomerEmailOrderBySaleTimeDesc(String customerEmail);
}
