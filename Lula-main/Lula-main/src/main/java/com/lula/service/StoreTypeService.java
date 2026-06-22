package com.lula.service;

import com.lula.dto.StoreTypeResponse;
import com.lula.repository.StoreTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreTypeService {

    private final StoreTypeRepository storeTypeRepository;

    public List<StoreTypeResponse> getAllStoreTypes() {
        return storeTypeRepository.findAll().stream()
                .map(st -> StoreTypeResponse.builder()
                        .id(st.getId())
                        .name(st.getName())
                        .emoji(st.getEmoji())
                        .description(st.getDescription())
                        .tip(st.getTip())
                        .showExpiry(st.getShowExpiry())
                        .categories(st.getCategories().stream().map(c -> c.getName()).collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
}
