package com.bryanho.identityservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bryanho.identityservice.dto.request.RoleRequest;
import com.bryanho.identityservice.dto.response.RoleResponse;
import com.bryanho.identityservice.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
