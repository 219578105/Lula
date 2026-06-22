package com.lula.repository;

import com.lula.entity.CreditRequest;
import com.lula.enums.CreditStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRequestRepository extends JpaRepository<CreditRequest, Long> {
    List<CreditRequest> findByBusinessIdOrderByRequestedAtDesc(Long businessId);
    List<CreditRequest> findByCustomerEmailOrderByRequestedAtDesc(String customerEmail);
    List<CreditRequest> findByBusinessIdAndStatusOrderByRequestedAtDesc(Long businessId, CreditStatus status);
    long countByBusinessIdAndStatus(Long businessId, CreditStatus status);
}
