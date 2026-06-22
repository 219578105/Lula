package com.lula.controller;

import com.lula.dto.ApiResponse;
import com.lula.dto.ProductRequest;
import com.lula.dto.ProductResponse;
import com.lula.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request,
            Authentication authentication) {
        ProductResponse response = productService.createProduct(request, authentication);
        return ResponseEntity.ok(ApiResponse.success("Product added", response));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getMyProducts(Authentication authentication) {
        List<ProductResponse> products = productService.getMyProducts(authentication);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getPublicProducts(
            @RequestParam(required = false) Long businessId) {
        List<ProductResponse> products = productService.getPublicProducts(businessId);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            Authentication authentication) {
        ProductResponse response = productService.updateProduct(id, request, authentication);
        return ResponseEntity.ok(ApiResponse.success("Product updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            Authentication authentication) {
        productService.deleteProduct(id, authentication);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }

    @GetMapping("/alerts/low-stock")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getLowStock(Authentication authentication) {
        List<ProductResponse> products = productService.getLowStock(authentication);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/alerts/expiring")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getExpiringSoon(Authentication authentication) {
        List<ProductResponse> products = productService.getExpiringSoon(authentication);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
}
