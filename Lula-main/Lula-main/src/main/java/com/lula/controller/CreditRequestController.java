package com.lula.controller;

import com.lula.dto.ApiResponse;
import com.lula.dto.CreditRequestDto;
import com.lula.dto.CreditRequestResponse;
import com.lula.security.UserDetailsImpl;
import com.lula.service.CreditRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/credit-requests")
@RequiredArgsConstructor
public class CreditRequestController {

    private final CreditRequestService creditRequestService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreditRequestResponse>> createCreditRequest(
            @Valid @RequestBody CreditRequestDto request,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        CreditRequestResponse response = creditRequestService.createCreditRequest(
                request, userDetails.getEmail(), userDetails.getName());
        return ResponseEntity.ok(ApiResponse.success("Credit request sent", response));
    }

    @GetMapping("/business")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<CreditRequestResponse>>> getBusinessCreditRequests(Authentication authentication) {
        List<CreditRequestResponse> requests = creditRequestService.getBusinessCreditRequests(authentication);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    @GetMapping("/customer")
    public ResponseEntity<ApiResponse<List<CreditRequestResponse>>> getCustomerCreditRequests(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<CreditRequestResponse> requests = creditRequestService.getCustomerCreditRequests(userDetails.getEmail());
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    @PostMapping("/{id}/handle")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<CreditRequestResponse>> handleCreditRequest(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String responseNote,
            Authentication authentication) {
        CreditRequestResponse response = creditRequestService.handleCreditRequest(id, status, responseNote, authentication);
        return ResponseEntity.ok(ApiResponse.success("Credit request updated", response));
    }

    @GetMapping("/pending/count")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Long>> countPending(Authentication authentication) {
        long count = creditRequestService.countPendingRequests(authentication);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
