package com.devteria.identityservice.service;

import java.util.HashSet;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.devteria.identityservice.dto.request.*;
import com.devteria.identityservice.dto.response.EateryMenuResponse;
import com.devteria.identityservice.entity.Eatery;
import com.devteria.identityservice.entity.FoodItem;
import com.devteria.identityservice.exception.AppException;
import com.devteria.identityservice.exception.ErrorCode;
import com.devteria.identityservice.mapper.EateryMapper;
import com.devteria.identityservice.mapper.FoodItemMapper;
import com.devteria.identityservice.repository.EateryRepository;
import com.devteria.identityservice.repository.FoodItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EateryService {
    private final EateryRepository eateryRepository;
    private final EateryMapper eateryMapper;
    private final FoodItemRepository foodItemRepository;
    private final FoodItemMapper foodItemMapper;

    public EateryMenuResponse createEatery(EateryRequest request) {
        // Check if an eatery with the same name already exists
        if (eateryRepository.existsById(request.getName())) {
            throw new AppException(ErrorCode.EATER_EXISTED);
        }

        var eatery = eateryMapper.toEatery(request);

        // var foodItems = foodItemRepository.findAllById(request.getFoodItems());
        var foodItems = new HashSet<FoodItem>();

        // Save the eatery to the repository
        eatery = eateryRepository.save(eatery);

        var response = eateryMapper.toEateryMenuResponse(eatery);

        return response;
    }

    public EateryMenuResponse getEatery(String eateryName) {
        return eateryRepository
                .findById(eateryName)
                .map(eateryMapper::toEateryMenuResponse)
                .orElseThrow(() -> new RuntimeException("Eatery not found"));
    }

    public List<EateryMenuResponse> getAllEateries() {
        return eateryRepository.findAll().stream()
                .map(eateryMapper::toEateryMenuResponse)
                .toList();
    }

    public EateryMenuResponse addFoodItemToEatery(String eateryName, FoodItemRequest foodItemRequest) {
        // Fetch the eatery by name
        var eatery = eateryRepository.findById(eateryName)
                .orElseThrow(() -> new AppException(ErrorCode.EATERY_NOT_FOUND));

        // Check if the food item already exists
        var foodItem = foodItemRepository.findById(foodItemRequest.getName()).orElse(null);
        if (foodItem == null) {
            // Create a new FoodItem if it does not exist
            foodItem = foodItemMapper.toFoodItem(foodItemRequest);
            foodItem = foodItemRepository.save(foodItem);
        }

        // Check if the food item is already associated with the eatery
        if (!eatery.getFoodItems().contains(foodItem)) {
            eatery.getFoodItems().add(foodItem);
            eateryRepository.save(eatery);
        } else {
            throw new AppException(ErrorCode.FOOD_ITEM_ALREADY_EXISTS);
        }
        return eateryMapper.toEateryMenuResponse(eateryRepository.save(eatery));
    }

    public EateryMenuResponse updateEatery(String eateryName, EaterUpdateRequest request) {
        Eatery eatery = eateryRepository.findById(eateryName)
                .orElseThrow(() -> new AppException(ErrorCode.EATERY_NOT_FOUND));

        eateryMapper.updateEatery(eatery, request);
        eatery.setContactNumber(request.getContactNumber());
        eatery.setLocation(request.getLocation());

        return eateryMapper.toEateryMenuResponse(eateryRepository.save(eatery));
    }

    public void deleteEatery(String eateryName) {
        eateryRepository.deleteById(eateryName);
    }

    @Transactional
    public void deleteFoodItemsFromEatery(String eateryName, String foodName) {
        Eatery eatery = eateryRepository.findById(eateryName)
                .orElseThrow(() -> new AppException(ErrorCode.EATERY_NOT_FOUND));

        FoodItem foodItem = foodItemRepository.findById(foodName)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_ITEM_NOT_FOUND));

        // Since the relationship is unidirectional, only update from Eatery side
        eatery.getFoodItems().remove(foodItem);

        eateryRepository.save(eatery);
    }

}
