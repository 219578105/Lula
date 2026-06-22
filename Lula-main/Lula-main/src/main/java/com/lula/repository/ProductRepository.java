package com.lula.repository;

import com.lula.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByBusinessId(Long businessId);
    List<Product> findByBusinessIdAndIsActiveTrue(Long businessId);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(:businessId IS NULL OR p.business.id = :businessId)")
    List<Product> findPublicProducts(@Param("businessId") Long businessId);

    @Query("SELECT p FROM Product p WHERE p.business.id = :businessId AND p.stock < 5 AND p.isActive = true")
    List<Product> findLowStockByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT p FROM Product p WHERE p.business.id = :businessId AND p.expiryDate <= :date AND p.isActive = true")
    List<Product> findExpiringSoonByBusinessId(@Param("businessId") Long businessId, @Param("date") LocalDate date);
}
