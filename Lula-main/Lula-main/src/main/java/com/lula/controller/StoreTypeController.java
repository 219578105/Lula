package com.lula.controller;

import com.lula.dto.ApiResponse;
import com.lula.dto.StoreTypeResponse;
import com.lula.service.StoreTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/store-types")
@RequiredArgsConstructor
public class StoreTypeController {

    private final StoreTypeService storeTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreTypeResponse>>> getAllStoreTypes() {
        List<StoreTypeResponse> types = storeTypeService.getAllStoreTypes();
        return ResponseEntity.ok(ApiResponse.success(types));
    }
}
