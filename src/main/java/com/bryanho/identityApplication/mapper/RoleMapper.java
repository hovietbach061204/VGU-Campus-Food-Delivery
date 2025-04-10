package com.bryanho.identityApplication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bryanho.identityApplication.dto.request.RoleRequest;
import com.bryanho.identityApplication.dto.response.RoleResponse;
import com.bryanho.identityApplication.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
