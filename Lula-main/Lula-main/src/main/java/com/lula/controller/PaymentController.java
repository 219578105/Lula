package com.lula.controller;

import com.lula.dto.ApiResponse;
import com.lula.dto.PaymentRequest;
import com.lula.dto.PaymentResponse;
import com.lula.security.UserDetailsImpl;
import com.lula.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> recordPayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        PaymentResponse response = paymentService.recordPayment(
                request, userDetails.getEmail(), userDetails.getName());
        return ResponseEntity.ok(ApiResponse.success("Payment recorded", response));
    }

    @GetMapping("/business")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getBusinessPayments(Authentication authentication) {
        List<PaymentResponse> payments = paymentService.getBusinessPayments(authentication);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @GetMapping("/customer")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getCustomerPayments(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<PaymentResponse> payments = paymentService.getCustomerPayments(userDetails.getEmail());
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @PathVariable Long id,
            Authentication authentication) {
        PaymentResponse response = paymentService.confirmPayment(id, authentication);
        return ResponseEntity.ok(ApiResponse.success("Payment confirmed", response));
    }
}
