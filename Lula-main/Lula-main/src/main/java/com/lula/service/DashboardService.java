package com.lula.service;

import com.lula.dto.AlertDto;
import com.lula.dto.DashboardStats;
import com.lula.entity.Business;
import com.lula.entity.Product;
import com.lula.repository.CreditRequestRepository;
import com.lula.repository.ProductRepository;
import com.lula.repository.SaleRepository;
import com.lula.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CreditRequestRepository creditRequestRepository;
    private final BusinessService businessService;

    public DashboardStats getStats(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());
        Long businessId = business.getId();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        BigDecimal todayRevenue = saleRepository.sumTodayRevenue(businessId, startOfDay, endOfDay);
        int todayTransactions = saleRepository.findTodaySales(businessId, startOfDay, endOfDay).size();
        int totalProducts = productRepository.findByBusinessId(businessId).size();
        int totalCustomers = business.getCustomers().size();
        int pendingSync = 0;
        int lowStockCount = productRepository.findLowStockByBusinessId(businessId).size();
        int expiringCount = productRepository.findExpiringSoonByBusinessId(businessId, today.plusDays(7)).size();
        long creditRequestsCount = creditRequestRepository.countByBusinessIdAndStatus(businessId, com.lula.enums.CreditStatus.PENDING);

        List<AlertDto> alerts = new ArrayList<>();

        List<Product> lowStock = productRepository.findLowStockByBusinessId(businessId);
        for (Product p : lowStock) {
            String msg = p.getStock() == 0
                    ? String.format("\"%s\" is out of stock!", p.getName())
                    : String.format("\"%s\" is low -- only %d left.", p.getName(), p.getStock());
            alerts.add(AlertDto.builder()
                    .type("stock")
                    .message(msg)
                    .severity(p.getStock() == 0 ? "danger" : "warning")
                    .relatedId(p.getId())
                    .build());
        }

        List<Product> expiring = productRepository.findExpiringSoonByBusinessId(businessId, today.plusDays(7));
        for (Product p : expiring) {
            int days = (int) java.time.temporal.ChronoUnit.DAYS.between(today, p.getExpiryDate());
            String msg;
            String severity;
            if (days <= 0) {
                msg = String.format("\"%s\" has EXPIRED! Remove from shelves immediately.", p.getName());
                severity = "danger";
            } else if (days <= 3) {
                msg = String.format("\"%s\" expires in %d day%s!", p.getName(), days, days > 1 ? "s" : "");
                severity = "danger";
            } else {
                msg = String.format("\"%s\" expires in %d days.", p.getName(), days);
                severity = "warning";
            }
            alerts.add(AlertDto.builder()
                    .type("expiry")
                    .message(msg)
                    .severity(severity)
                    .relatedId(p.getId())
                    .build());
        }

        business.getCustomers().stream()
                .filter(c -> c.getCreditLimit() != null && c.getCreditLimit().compareTo(BigDecimal.ZERO) > 0
                        && c.getBalance().compareTo(c.getCreditLimit()) >= 0)
                .forEach(c -> alerts.add(AlertDto.builder()
                        .type("credit")
                        .message(String.format("%s has reached their credit limit of R%s.", c.getName(), c.getCreditLimit()))
                        .severity("danger")
                        .relatedId(c.getId())
                        .build()));

        return DashboardStats.builder()
                .todayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
                .todayTransactions(todayTransactions)
                .totalProducts(totalProducts)
                .totalCustomers(totalCustomers)
                .pendingSync(pendingSync)
                .lowStockCount(lowStockCount)
                .expiringCount(expiringCount)
                .creditRequestsCount((int) creditRequestsCount)
                .alerts(alerts)
                .build();
    }
}
