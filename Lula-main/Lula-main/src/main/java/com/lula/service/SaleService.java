package com.lula.service;

import com.lula.dto.SaleRequest;
import com.lula.dto.SaleResponse;
import com.lula.dto.SaleItemResponse;
import com.lula.entity.Business;
import com.lula.entity.Customer;
import com.lula.entity.Product;
import com.lula.entity.Sale;
import com.lula.entity.SaleItem;
import com.lula.repository.CustomerRepository;
import com.lula.repository.ProductRepository;
import com.lula.repository.SaleRepository;
import com.lula.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final BusinessService businessService;

    @Transactional
    public SaleResponse recordSale(SaleRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());

        Sale sale = Sale.builder()
                .description(request.getDescription())
                .total(request.getTotal())
                .cashReceived(request.getCashReceived() != null ? request.getCashReceived() : request.getTotal())
                .changeAmount(request.getChangeAmount())
                .saleTime(LocalDateTime.now())
                .isSynced(true)
                .business(business)
                .build();

        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId()).orElse(null);
            if (customer != null && customer.getBusiness().getId().equals(business.getId())) {
                sale.setCustomer(customer);
            }
        }

        sale = saleRepository.save(sale);

        if (request.getItems() != null) {
            for (var itemReq : request.getItems()) {
                Product product = productRepository.findById(itemReq.getProductId()).orElse(null);
                if (product != null && product.getBusiness().getId().equals(business.getId())) {
                    product.setStock(product.getStock() - itemReq.getQuantity());
                    productRepository.save(product);

                    SaleItem item = SaleItem.builder()
                            .quantity(itemReq.getQuantity())
                            .unitPrice(itemReq.getUnitPrice())
                            .totalPrice(itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())))
                            .sale(sale)
                            .product(product)
                            .build();
                    sale.getItems().add(item);
                }
            }
        }

        sale = saleRepository.save(sale);
        return mapToResponse(sale);
    }

    public List<SaleResponse> getMySales(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());
        return saleRepository.findByBusinessIdOrderBySaleTimeDesc(business.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<SaleResponse> getTodaySales(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());
        LocalDate today = LocalDate.now();
        return saleRepository.findTodaySales(business.getId(), today.atStartOfDay(), today.plusDays(1).atStartOfDay())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public BigDecimal getTodayRevenue(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());
        LocalDate today = LocalDate.now();
        return saleRepository.sumTodayRevenue(business.getId(), today.atStartOfDay(), today.plusDays(1).atStartOfDay());
    }

    public List<SaleResponse> getCustomerSales(String customerEmail) {
        return saleRepository.findByCustomerEmailOrderBySaleTimeDesc(customerEmail)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private SaleResponse mapToResponse(Sale s) {
        return SaleResponse.builder()
                .id(s.getId())
                .description(s.getDescription())
                .total(s.getTotal())
                .cashReceived(s.getCashReceived())
                .changeAmount(s.getChangeAmount())
                .saleTime(s.getSaleTime())
                .isSynced(s.getIsSynced())
                .customerName(s.getCustomer() != null ? s.getCustomer().getName() : null)
                .businessId(s.getBusiness() != null ? s.getBusiness().getId() : null)
                .businessName(s.getBusiness() != null ? s.getBusiness().getBizName() : null)
                .items(s.getItems().stream().map(this::mapItemResponse).collect(Collectors.toList()))
                .build();
    }

    private SaleItemResponse mapItemResponse(SaleItem item) {
        return SaleItemResponse.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .build();
    }
}
