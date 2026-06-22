package com.lula.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "store_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreType extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 10)
    private String emoji;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String tip;

    @Column(name = "show_expiry", nullable = false)
    private Boolean showExpiry = true;

    @OneToMany(mappedBy = "storeType", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "storeType", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Business> businesses = new ArrayList<>();
}
