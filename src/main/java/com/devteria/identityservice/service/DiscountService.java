package com.devteria.identityservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devteria.identityservice.dto.request.DiscountRequest;
import com.devteria.identityservice.dto.response.DiscountResponse;
import com.devteria.identityservice.entity.Discount;
import com.devteria.identityservice.mapper.DiscountMapper;
import com.devteria.identityservice.repository.DiscountRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;

    public DiscountResponse createDiscount(DiscountRequest request) {
        Discount discount = discountMapper.toDiscount(request);
        discount = discountRepository.save(discount);
        return discountMapper.toDiscountResponse(discount);
    }

    public List<DiscountResponse> getAllDiscounts() {
        return discountRepository.findAll().stream()
                .map(discountMapper::toDiscountResponse)
                .toList();
    }

    public DiscountResponse getDiscount(String discountId) {
        return discountRepository
                .findById(discountId)
                .map(discountMapper::toDiscountResponse)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
    }

    public void deleteDiscount(String discountId) {
        discountRepository.deleteById(discountId);
    }
}
