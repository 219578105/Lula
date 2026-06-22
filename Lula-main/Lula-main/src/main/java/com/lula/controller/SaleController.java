package com.lula.controller;

import com.lula.dto.ApiResponse;
import com.lula.dto.SaleRequest;
import com.lula.dto.SaleResponse;
import com.lula.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<SaleResponse>> recordSale(
            @Valid @RequestBody SaleRequest request,
            Authentication authentication) {
        SaleResponse response = saleService.recordSale(request, authentication);
        return ResponseEntity.ok(ApiResponse.success("Sale recorded", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<SaleResponse>>> getMySales(Authentication authentication) {
        List<SaleResponse> sales = saleService.getMySales(authentication);
        return ResponseEntity.ok(ApiResponse.success(sales));
    }

    @GetMapping("/today")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<SaleResponse>>> getTodaySales(Authentication authentication) {
        List<SaleResponse> sales = saleService.getTodaySales(authentication);
        return ResponseEntity.ok(ApiResponse.success(sales));
    }

    @GetMapping("/today/revenue")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<BigDecimal>> getTodayRevenue(Authentication authentication) {
        BigDecimal revenue = saleService.getTodayRevenue(authentication);
        return ResponseEntity.ok(ApiResponse.success(revenue));
    }

    @GetMapping("/customer/{email}")
    public ResponseEntity<ApiResponse<List<SaleResponse>>> getCustomerSales(@PathVariable String email) {
        List<SaleResponse> sales = saleService.getCustomerSales(email);
        return ResponseEntity.ok(ApiResponse.success(sales));
    }
}
