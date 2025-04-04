package com.identityApplication.IdentityApplication.mapper;

import com.identityApplication.IdentityApplication.dto.request.RoleRequest;
import com.identityApplication.IdentityApplication.dto.response.RoleResponse;
import com.identityApplication.IdentityApplication.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}