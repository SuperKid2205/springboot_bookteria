package com.hung.practice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hung.practice.dto.request.PermissionRequest;
import com.hung.practice.dto.response.ApiResponse;
import com.hung.practice.dto.response.PermissionResponse;
import com.hung.practice.service.PermissionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getPermissions() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getPermisstions())
                .build();
    }

    @GetMapping("/{permissionName}")
    public ApiResponse<PermissionResponse> getPermission(@PathVariable("permissionName") String permissionName) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.getPermisstion(permissionName))
                .build();
    }

    @PostMapping
    public ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.createPermission(request))
                .build();
    }

    @DeleteMapping("/{permissionName}")
    public ApiResponse<String> deletePermission(@PathVariable("permissionName") String permissionName) {
        permissionService.deletePermission(permissionName);
        return ApiResponse.<String>builder().message("Permission deleted.").build();
    }

    @DeleteMapping
    public ApiResponse<String> deleteAllPermissions() {
        permissionService.deleteAllPermissions();
        return ApiResponse.<String>builder().message("All Permissions deleted.").build();
    }
}
