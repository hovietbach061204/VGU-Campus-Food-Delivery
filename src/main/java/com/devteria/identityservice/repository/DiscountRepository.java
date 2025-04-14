package com.devteria.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devteria.identityservice.entity.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String> {}
