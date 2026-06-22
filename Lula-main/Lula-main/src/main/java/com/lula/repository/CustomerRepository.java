package com.lula.repository;

import com.lula.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByBusinessId(Long businessId);
    Optional<Customer> findByEmailAndBusinessId(String email, Long businessId);

    @Query("SELECT c FROM Customer c WHERE c.business.id = :businessId AND c.creditLimit > 0 AND c.balance >= c.creditLimit")
    List<Customer> findOverLimitCustomers(@Param("businessId") Long businessId);
}
