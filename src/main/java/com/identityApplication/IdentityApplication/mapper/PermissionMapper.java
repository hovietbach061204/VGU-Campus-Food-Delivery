package com.identityApplication.IdentityApplication.mapper;

import org.mapstruct.Mapper;

import com.identityApplication.IdentityApplication.dto.request.PermissionRequest;
import com.identityApplication.IdentityApplication.dto.response.PermissionResponse;
import com.identityApplication.IdentityApplication.entity.Permission;

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
