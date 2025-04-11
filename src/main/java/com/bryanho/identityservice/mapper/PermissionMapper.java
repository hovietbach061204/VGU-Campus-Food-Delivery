package com.bryanho.identityservice.mapper;

import org.mapstruct.Mapper;

import com.bryanho.identityservice.dto.request.PermissionRequest;
import com.bryanho.identityservice.dto.response.PermissionResponse;
import com.bryanho.identityservice.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
