package com.lula.controller;

import com.lula.dto.ApiResponse;
import com.lula.dto.CustomerRequest;
import com.lula.dto.CustomerResponse;
import com.lula.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CustomerRequest request,
            Authentication authentication) {
        CustomerResponse response = customerService.createCustomer(request, authentication);
        return ResponseEntity.ok(ApiResponse.success("Customer added", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getMyCustomers(Authentication authentication) {
        List<CustomerResponse> customers = customerService.getMyCustomers(authentication);
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomer(
            @PathVariable Long id,
            Authentication authentication) {
        CustomerResponse response = customerService.getCustomer(id, authentication);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request,
            Authentication authentication) {
        CustomerResponse response = customerService.updateCustomer(id, request, authentication);
        return ResponseEntity.ok(ApiResponse.success("Customer updated", response));
    }

    @PostMapping("/{id}/balance")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> adjustBalance(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            Authentication authentication) {
        CustomerResponse response = customerService.adjustBalance(id, amount, authentication);
        return ResponseEntity.ok(ApiResponse.success("Balance updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @PathVariable Long id,
            Authentication authentication) {
        customerService.deleteCustomer(id, authentication);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted", null));
    }

    @GetMapping("/alerts/over-limit")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getOverLimitCustomers(Authentication authentication) {
        List<CustomerResponse> customers = customerService.getOverLimitCustomers(authentication);
        return ResponseEntity.ok(ApiResponse.success(customers));
    }
}
