package com.devteria.identityservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devteria.identityservice.dto.request.FoodItemRequest;
import com.devteria.identityservice.dto.response.FoodItemMenuResponse;
import com.devteria.identityservice.entity.FoodItem;
import com.devteria.identityservice.exception.AppException;
import com.devteria.identityservice.exception.ErrorCode;
import com.devteria.identityservice.mapper.FoodItemMapper;
import com.devteria.identityservice.repository.FoodItemRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FoodItemService {
    final FoodItemRepository foodItemRepository;
    final FoodItemMapper foodItemMapper;

    public FoodItemMenuResponse createFoodItem(FoodItemRequest request) {
        FoodItem foodItem = foodItemMapper.toFoodItem(request);
        foodItem = foodItemRepository.save(foodItem);
        return foodItemMapper.toFoodItemMenuResponse(foodItem);
    }

    public List<FoodItemMenuResponse> getAllFoodItems() {
        return foodItemRepository.findAll().stream()
                .map(foodItemMapper::toFoodItemMenuResponse)
                .toList();
    }

    public FoodItemMenuResponse getFoodItem(String foodItemId) {
        return foodItemMapper.toFoodItemMenuResponse(foodItemRepository
                .findById(foodItemId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public void deleteFoodItem(String foodItemId) {
        foodItemRepository.deleteById(foodItemId);
    }
}
