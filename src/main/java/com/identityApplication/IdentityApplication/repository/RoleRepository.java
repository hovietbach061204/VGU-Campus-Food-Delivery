package com.identityApplication.IdentityApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.identityApplication.IdentityApplication.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}
