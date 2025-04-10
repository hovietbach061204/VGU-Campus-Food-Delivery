package com.bryanho.identityApplication.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bryanho.identityApplication.dto.request.PermissionRequest;
import com.bryanho.identityApplication.dto.response.PermissionResponse;
import com.bryanho.identityApplication.entity.Permission;
import com.bryanho.identityApplication.mapper.PermissionMapper;
import com.bryanho.identityApplication.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor // Autowired Bean
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
