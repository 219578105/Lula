package com.lula.controller;

import com.lula.dto.ApiResponse;
import com.lula.dto.AuthResponse;
import com.lula.dto.GoogleAuthRequest;
import com.lula.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleAuth(@Valid @RequestBody GoogleAuthRequest request) {
        try {
            AuthResponse response = authService.authenticateWithGoogle(request);
            return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid Google token: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestParam String refreshToken) {
        try {
            AuthResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid refresh token: " + e.getMessage()));
        }
    }
}
