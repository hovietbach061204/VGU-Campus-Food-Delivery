package com.devteria.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devteria.identityservice.entity.FoodItem;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, String> {}
