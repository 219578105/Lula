package com.lula.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopPublicResponse {
    private Long id;
    private String bizName;
    private String storeType;
    private String storeTypeEmoji;
    private String phone;
    private String address;
    private String city;
    private boolean isOpen;
    private Double rating;
    private Integer reviewCount;
    private String ownerName;
    private List<ProductResponse> products;
}
