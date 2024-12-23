package com.hung.practice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.hung.practice.dto.request.UserCreationRequest;
import com.hung.practice.dto.request.UserUpdateRequest;
import com.hung.practice.dto.response.UserResponse;
import com.hung.practice.entity.UserEntity;

@Mapper
public interface UserMapper {

    UserEntity toUser(UserCreationRequest request);

    UserResponse toUserResponse(UserEntity user);

    UserEntity toUpdateUser(@MappingTarget UserEntity user, UserUpdateRequest request);
}
