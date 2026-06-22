package com.lula.repository;

import com.lula.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    Optional<Business> findByOwnerId(Long ownerId);
    List<Business> findByIsOpenTrue();

    @Query("SELECT b FROM Business b WHERE b.isOpen = true AND " +
           "(:storeType IS NULL OR b.storeType.name = :storeType)")
    List<Business> findPublicShops(@Param("storeType") String storeType);
}
