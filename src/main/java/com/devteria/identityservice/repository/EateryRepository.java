package com.devteria.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devteria.identityservice.entity.Eatery;

@Repository
public interface EateryRepository extends JpaRepository<Eatery, String> {}
