package com.hung.practice.mapper;

import com.hung.practice.dto.request.ProfileCreationRequest;
import com.hung.practice.dto.request.UserCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);

}
