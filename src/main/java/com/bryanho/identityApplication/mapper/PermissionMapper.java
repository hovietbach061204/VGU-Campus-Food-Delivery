package com.bryanho.identityApplication.mapper;

import org.mapstruct.Mapper;

import com.bryanho.identityApplication.dto.request.PermissionRequest;
import com.bryanho.identityApplication.dto.response.PermissionResponse;
import com.bryanho.identityApplication.entity.Permission;

// @Mapper(componentModel = "spring") // we notify the map struct that we generate this map
//// in spring
// public interface PermissionMapper {
//    Permission toPermission(PermissionRequest request);
//    PermissionResponse toPermissionResponse(Permission permission);
// }

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
