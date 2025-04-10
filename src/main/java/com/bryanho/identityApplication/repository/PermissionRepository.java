package com.bryanho.identityApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bryanho.identityApplication.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {}
