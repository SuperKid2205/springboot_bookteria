package com.hung.practice.controller;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import com.hung.practice.dto.request.RoleRequest;
import com.hung.practice.dto.response.ApiResponse;
import com.hung.practice.dto.response.RoleResponse;
import com.hung.practice.service.RoleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @GetMapping
    public ApiResponse<List<RoleResponse>> getRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getRoles())
                .build();
    }

    @GetMapping("/{roleName}")
    public ApiResponse<RoleResponse> getRole(@PathVariable("roleName") String roleName) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.getRole(roleName))
                .build();
    }

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(request))
                .build();
    }

    @DeleteMapping("/{roleName}")
    public ApiResponse<String> deleteUser(@PathVariable("roleName") String roleName) {
        roleService.deleteRole(roleName);
        return ApiResponse.<String>builder().message("Role deleted.").build();
    }

    @DeleteMapping
    public ApiResponse<String> deleteAllRoles() {
        roleService.deleteAllRoles();
        return ApiResponse.<String>builder().message("All Roles deleted.").build();
    }
}
