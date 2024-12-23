package com.hung.practice.mapper;

import org.mapstruct.Mapper;

import com.hung.practice.dto.request.PermissionRequest;
import com.hung.practice.dto.response.PermissionResponse;
import com.hung.practice.entity.Permission;

@Mapper
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
