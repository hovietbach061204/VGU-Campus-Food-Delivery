package com.bryanho.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bryanho.identityservice.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {}
