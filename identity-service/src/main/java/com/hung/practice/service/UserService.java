package com.hung.practice.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hung.practice.dto.request.UserCreationRequest;
import com.hung.practice.dto.request.UserUpdateRequest;
import com.hung.practice.dto.response.UserResponse;
import com.hung.practice.entity.Role;
import com.hung.practice.entity.UserEntity;
import com.hung.practice.enums.ErrorCode;
import com.hung.practice.exception.AppException;
import com.hung.practice.mapper.UserMapper;
import com.hung.practice.repository.RoleRepository;
import com.hung.practice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PostAuthorize("hasRole('ADMIN') or returnObject.username == authentication.name")
    public UserResponse getUser(String id) {

        // Check exist user
        UserEntity userEntity =
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXITS));

        return userMapper.toUserResponse(userEntity);
    }

    public UserResponse getMyInfo() {

        var contextHolder = SecurityContextHolder.getContext().getAuthentication();
        String username = contextHolder.getName();

        UserEntity userEntity =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXITS));

        return userMapper.toUserResponse(userEntity);
    }

    public UserResponse createUser(UserCreationRequest request) {
        log.info("Service: createUser");
        // Check exist Username
        boolean existUserName = userRepository.existsByUsername(request.getUsername());
        if (existUserName) {
            throw new AppException(ErrorCode.USER_EXITS);
        }

        // Mapping request to entity
        UserEntity userEntity = userMapper.toUser(request);

        // Encode password
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));

        // Setting default role for new user
        Set<Role> roles = new HashSet<>();
        Optional<Role> userRole = roleRepository.findById("USER");
        if (userRole.isPresent()) {
            roles.add(userRole.get());
            userEntity.setRoles(roles);
        }

        // Save
        return userMapper.toUserResponse(userRepository.save(userEntity));
    }

    @PostAuthorize("hasRole('ADMIN') or returnObject.username == authentication.name")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        // Get user
        UserEntity targetUser =
                userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXITS));
        UserEntity updateUser = userMapper.toUpdateUser(targetUser, request);

        // Save
        return userMapper.toUserResponse(userRepository.save(updateUser));
    }

    public void deleteUser(String userId) {
        // Get User
        userRepository.findById(userId).ifPresent(user -> {
            throw new AppException(ErrorCode.USER_NOT_EXITS);
        });

        // Delete User
        userRepository.deleteById(userId);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }
}
