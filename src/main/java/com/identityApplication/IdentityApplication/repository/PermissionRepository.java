package com.identityApplication.IdentityApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.identityApplication.IdentityApplication.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {}
