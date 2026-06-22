package com.lula.service;

import com.lula.dto.ProductRequest;
import com.lula.dto.ProductResponse;
import com.lula.entity.Business;
import com.lula.entity.Category;
import com.lula.entity.Product;
import com.lula.repository.CategoryRepository;
import com.lula.repository.ProductRepository;
import com.lula.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BusinessService businessService;

    @Transactional
    public ProductResponse createProduct(ProductRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .expiryDate(request.getExpiryDate())
                .imageUrl(request.getImageUrl())
                .isActive(true)
                .business(business)
                .build();

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);
            product.setCategory(category);
        }

        product = productRepository.save(product);
        return mapToResponse(product);
    }

    public List<ProductResponse> getMyProducts(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());
        return productRepository.findByBusinessId(business.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ProductResponse> getPublicProducts(Long businessId) {
        return productRepository.findPublicProducts(businessId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getBusiness().getId().equals(business.getId())) {
            throw new RuntimeException("Unauthorized: Product does not belong to your business");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setExpiryDate(request.getExpiryDate());
        product.setImageUrl(request.getImageUrl());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);
            product.setCategory(category);
        }

        product = productRepository.save(product);
        return mapToResponse(product);
    }

    @Transactional
    public void deleteProduct(Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getBusiness().getId().equals(business.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        product.setIsActive(false);
        productRepository.save(product);
    }

    public List<ProductResponse> getLowStock(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());
        return productRepository.findLowStockByBusinessId(business.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ProductResponse> getExpiringSoon(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());
        return productRepository.findExpiringSoonByBusinessId(business.getId(), LocalDate.now().plusDays(7))
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private ProductResponse mapToResponse(Product p) {
        Integer daysUntilExpiry = null;
        String status = "In Stock";

        if (p.getStock() == 0) {
            status = "Out of Stock";
        } else if (p.getStock() < 5) {
            status = "Low Stock";
        }

        if (p.getExpiryDate() != null) {
            daysUntilExpiry = (int) ChronoUnit.DAYS.between(LocalDate.now(), p.getExpiryDate());
            if (daysUntilExpiry <= 0) {
                status = "Expired";
            } else if (daysUntilExpiry <= 3) {
                status = "Expires Soon";
            }
        }

        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stock(p.getStock())
                .expiryDate(p.getExpiryDate())
                .imageUrl(p.getImageUrl())
                .category(p.getCategory() != null ? p.getCategory().getName() : null)
                .isActive(p.getIsActive())
                .businessId(p.getBusiness() != null ? p.getBusiness().getId() : null)
                .businessName(p.getBusiness() != null ? p.getBusiness().getBizName() : null)
                .daysUntilExpiry(daysUntilExpiry)
                .status(status)
                .build();
    }
}
