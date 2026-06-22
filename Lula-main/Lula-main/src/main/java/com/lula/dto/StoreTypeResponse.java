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
public class StoreTypeResponse {
    private Long id;
    private String name;
    private String emoji;
    private String description;
    private String tip;
    private boolean showExpiry;
    private List<String> categories;
}
