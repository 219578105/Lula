package com.lula.controller;

import com.lula.dto.ApiResponse;
import com.lula.dto.BusinessResponse;
import com.lula.dto.BusinessSetupRequest;
import com.lula.dto.ShopPublicResponse;
import com.lula.service.BusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping("/setup")
    public ResponseEntity<ApiResponse<BusinessResponse>> setupBusiness(
            @Valid @RequestBody BusinessSetupRequest request,
            Authentication authentication) {
        BusinessResponse response = businessService.setupBusiness(request, authentication);
        return ResponseEntity.ok(ApiResponse.success("Business setup complete", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<BusinessResponse>> getMyBusiness(Authentication authentication) {
        BusinessResponse response = businessService.getMyBusiness(authentication);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/shops")
    public ResponseEntity<ApiResponse<List<ShopPublicResponse>>> getPublicShops(
            @RequestParam(required = false) String storeType) {
        List<ShopPublicResponse> shops = businessService.getPublicShops(storeType);
        return ResponseEntity.ok(ApiResponse.success(shops));
    }
}
