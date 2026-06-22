package com.lula.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessResponse {
    private Long id;
    private String bizName;
    private String description;
    private String phone;
    private String address;
    private String city;
    private String storeType;
    private String storeTypeEmoji;
    private boolean isVerified;
    private boolean isOpen;
    private Double rating;
    private Integer reviewCount;
    private String ownerName;
    private String ownerEmail;
}
