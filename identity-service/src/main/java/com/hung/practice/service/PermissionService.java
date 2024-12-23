package com.hung.practice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hung.practice.dto.request.PermissionRequest;
import com.hung.practice.dto.response.PermissionResponse;
import com.hung.practice.entity.Permission;
import com.hung.practice.enums.ErrorCode;
import com.hung.practice.exception.AppException;
import com.hung.practice.mapper.PermissionMapper;
import com.hung.practice.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    PermissionMapper permissionMapper;
    PermissionRepository permissionRepository;

    public List<PermissionResponse> getPermisstions() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    public PermissionResponse getPermisstion(String name) {
        Permission permission =
                permissionRepository.findById(name).orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXITS));
        return permissionMapper.toPermissionResponse(permission);
    }

    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    public void deletePermission(String name) {
        permissionRepository.findById(name).ifPresent(permission -> {
            throw new AppException(ErrorCode.PERMISSION_NOT_EXITS);
        });
        permissionRepository.deleteById(name);
    }

    public void deleteAllPermissions() {
        permissionRepository.deleteAll();
    }
}
