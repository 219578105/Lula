package com.lula.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BusinessSetupRequest {
    @NotBlank(message = "Business name is required")
    private String bizName;

    private String description;
    private String phone;
    private String address;
    private String city;
    private String postalCode;

    @NotBlank(message = "Store type is required")
    private String storeType;
}
