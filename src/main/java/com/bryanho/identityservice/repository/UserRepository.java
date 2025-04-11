package com.bryanho.identityservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bryanho.identityservice.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
