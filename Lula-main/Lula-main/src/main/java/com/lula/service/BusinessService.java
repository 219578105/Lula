package com.lula.service;

import com.lula.dto.BusinessResponse;
import com.lula.dto.BusinessSetupRequest;
import com.lula.dto.ShopPublicResponse;
import com.lula.entity.Business;
import com.lula.entity.StoreType;
import com.lula.entity.User;
import com.lula.repository.BusinessRepository;
import com.lula.repository.StoreTypeRepository;
import com.lula.repository.UserRepository;
import com.lula.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final StoreTypeRepository storeTypeRepository;
    private final UserRepository userRepository;

    @Transactional
    public BusinessResponse setupBusiness(BusinessSetupRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        StoreType storeType = storeTypeRepository.findByName(request.getStoreType())
                .orElseThrow(() -> new RuntimeException("Store type not found: " + request.getStoreType()));

        Business business = Business.builder()
                .bizName(request.getBizName())
                .description(request.getDescription())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .storeType(storeType)
                .owner(user)
                .isVerified(false)
                .isOpen(true)
                .rating(0.0)
                .reviewCount(0)
                .build();

        business = businessRepository.save(business);
        return mapToResponse(business);
    }

    public BusinessResponse getMyBusiness(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessRepository.findByOwnerId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Business not found"));
        return mapToResponse(business);
    }

    public List<ShopPublicResponse> getPublicShops(String storeType) {
        List<Business> shops = businessRepository.findPublicShops(storeType);
        return shops.stream().map(this::mapToPublicResponse).collect(Collectors.toList());
    }

    public Business getBusinessByOwner(Long ownerId) {
        return businessRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Business not found"));
    }

    public Business getBusinessById(Long id) {
        return businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));
    }

    private BusinessResponse mapToResponse(Business b) {
        return BusinessResponse.builder()
                .id(b.getId())
                .bizName(b.getBizName())
                .description(b.getDescription())
                .phone(b.getPhone())
                .address(b.getAddress())
                .city(b.getCity())
                .storeType(b.getStoreType() != null ? b.getStoreType().getName() : null)
                .storeTypeEmoji(b.getStoreType() != null ? b.getStoreType().getEmoji() : null)
                .isVerified(b.getIsVerified())
                .isOpen(b.getIsOpen())
                .rating(b.getRating())
                .reviewCount(b.getReviewCount())
                .ownerName(b.getOwner() != null ? b.getOwner().getName() : null)
                .ownerEmail(b.getOwner() != null ? b.getOwner().getEmail() : null)
                .build();
    }

    private ShopPublicResponse mapToPublicResponse(Business b) {
        return ShopPublicResponse.builder()
                .id(b.getId())
                .bizName(b.getBizName())
                .storeType(b.getStoreType() != null ? b.getStoreType().getName() : null)
                .storeTypeEmoji(b.getStoreType() != null ? b.getStoreType().getEmoji() : null)
                .phone(b.getPhone())
                .address(b.getAddress())
                .city(b.getCity())
                .isOpen(b.getIsOpen())
                .rating(b.getRating())
                .reviewCount(b.getReviewCount())
                .ownerName(b.getOwner() != null ? b.getOwner().getName() : null)
                .build();
    }
}
