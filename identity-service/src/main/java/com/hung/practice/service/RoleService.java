package com.hung.practice.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hung.practice.dto.request.RoleRequest;
import com.hung.practice.dto.response.RoleResponse;
import com.hung.practice.entity.Role;
import com.hung.practice.enums.ErrorCode;
import com.hung.practice.exception.AppException;
import com.hung.practice.mapper.RoleMapper;
import com.hung.practice.repository.PermissionRepository;
import com.hung.practice.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {

    RoleMapper roleMapper;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    public List<RoleResponse> getRoles() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    public RoleResponse getRole(String name) {
        Role role = roleRepository.findById(name).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXITS));
        return roleMapper.toRoleResponse(role);
    }

    public RoleResponse createRole(RoleRequest request) {
        Role role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public void deleteRole(String name) {
        roleRepository.findById(name).ifPresent(role -> {
            throw new AppException(ErrorCode.ROLE_NOT_EXITS);
        });
        roleRepository.deleteById(name);
    }

    public void deleteAllRoles() {
        roleRepository.deleteAll();
    }
}
