package com.devteria.identityservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.devteria.identityservice.dto.request.RoleRequest;
import com.devteria.identityservice.dto.response.RoleOrderResponse;
import com.devteria.identityservice.dto.response.RoleResponse;
import com.devteria.identityservice.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleOrderResponse toRoleOrderResponse(Role role);

    RoleResponse toRoleResponse(Role role);
}
