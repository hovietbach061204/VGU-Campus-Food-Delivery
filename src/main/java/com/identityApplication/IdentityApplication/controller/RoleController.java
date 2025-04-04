package com.identityApplication.IdentityApplication.controller;

import com.identityApplication.IdentityApplication.dto.request.APIResponse;
import com.identityApplication.IdentityApplication.dto.request.RoleRequest;
import com.identityApplication.IdentityApplication.dto.response.RoleResponse;
import com.identityApplication.IdentityApplication.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @PostMapping
    APIResponse<RoleResponse> create(@RequestBody RoleRequest request) {
        return APIResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    APIResponse<List<RoleResponse>> getAll() {
        return APIResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    APIResponse<Void> delete(@PathVariable String role) {
        roleService.delete(role);
        return APIResponse.<Void>builder().build();
    }
}