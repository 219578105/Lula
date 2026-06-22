package com.lula.controller;

import com.lula.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "status", "UP",
                "service", "Lula API",
                "version", "1.0.0"
        )));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> info() {
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "name", "Lula",
                "description", "Smart business platform for South African shops",
                "features", new String[]{"Offline support", "POS", "Inventory", "Credit tracking", "Payments"},
                "storeTypes", 23
        )));
    }
}
