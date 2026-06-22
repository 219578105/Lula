package com.lula.repository;

import com.lula.entity.Payment;
import com.lula.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBusinessIdOrderByPaidAtDesc(Long businessId);
    List<Payment> findByCustomerEmailOrderByPaidAtDesc(String customerEmail);
    List<Payment> findByBusinessIdAndStatusOrderByPaidAtDesc(Long businessId, PaymentStatus status);
}
