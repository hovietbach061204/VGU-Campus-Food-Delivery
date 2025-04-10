package com.bryanho.identityApplication.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bryanho.identityApplication.dto.request.APIResponse;
import com.bryanho.identityApplication.dto.request.PermissionRequest;
import com.bryanho.identityApplication.dto.response.PermissionResponse;
import com.bryanho.identityApplication.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    APIResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return APIResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    APIResponse<List<PermissionResponse>> getAll() {
        return APIResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{permission}")
    APIResponse<Void> delete(@PathVariable String permission) {
        permissionService.delete(permission);
        return APIResponse.<Void>builder().build();
    }
}
