package com.devteria.identityservice.service;

import com.devteria.identityservice.dto.request.EateryRequest;
import com.devteria.identityservice.dto.response.EateryMenuResponse;
import com.devteria.identityservice.dto.response.EateryOrderResponse;
import com.devteria.identityservice.entity.Eatery;
import com.devteria.identityservice.mapper.EateryMapper;
import com.devteria.identityservice.repository.EateryRepository;
import com.devteria.identityservice.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EateryService {
    private final EateryRepository eateryRepository;
    private final EateryMapper eateryMapper;
    private final FoodItemRepository foodItemRepository;

    public EateryMenuResponse createEatery(EateryRequest request) {
        var eatery = eateryMapper.toEatery(request);

        var foodItems = foodItemRepository.findAllById(request.getFoodItems());
        eatery.setFoodItems(new HashSet<>(foodItems));

        eatery = eateryRepository.save(eatery);
        return eateryMapper.toEateryMenuResponse(eatery);
    }

    public EateryMenuResponse getEatery(String eateryName) {
        return eateryRepository.findById(eateryName)
                .map(eateryMapper::toEateryMenuResponse)
                .orElseThrow(() -> new RuntimeException("Eatery not found"));
    }

    public List<EateryMenuResponse> getAllEateries() {
        return eateryRepository.findAll().stream()
                .map(eateryMapper::toEateryMenuResponse)
                .toList();
    }

    public void deleteEatery(String eateryName) {
        eateryRepository.deleteById(eateryName);
    }
}