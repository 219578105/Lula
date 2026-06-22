package com.lula.controller;

import com.lula.dto.ApiResponse;
import com.lula.dto.DashboardStats;
import com.lula.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<DashboardStats>> getStats(Authentication authentication) {
        DashboardStats stats = dashboardService.getStats(authentication);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
