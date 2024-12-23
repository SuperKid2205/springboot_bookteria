package com.hung.practice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hung.practice.dto.request.RoleRequest;
import com.hung.practice.dto.response.RoleResponse;
import com.hung.practice.entity.Role;

@Mapper
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
