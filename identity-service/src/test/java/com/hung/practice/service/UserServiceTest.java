package com.hung.practice.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.hung.practice.dto.request.UserCreationRequest;
import com.hung.practice.dto.request.UserUpdateRequest;
import com.hung.practice.dto.response.UserResponse;
import com.hung.practice.entity.Role;
import com.hung.practice.entity.UserEntity;
import com.hung.practice.exception.AppException;
import com.hung.practice.repository.RoleRepository;
import com.hung.practice.repository.UserRepository;

@SpringBootTest
@TestPropertySource("/test.properties")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    private UserCreationRequest userCreationRequest;
    private UserUpdateRequest userUpdateRequest;
    private UserResponse userResponse;
    private UserEntity userEntity;
    private Role role;

    @BeforeEach
    void initData() {
        LocalDate dob = LocalDate.of(1991, 5, 22);
        userCreationRequest = UserCreationRequest.builder()
                .username("hung1")
                .password("321")
                .firstName("Hung")
                .lastName("Nguyen")
                .dob(LocalDate.of(1991, 5, 22))
                .build();

        userUpdateRequest = UserUpdateRequest.builder()
                .firstName("Thu")
                .lastName("Pham")
                .dob(LocalDate.of(1994, 11, 28))
                .build();

        userResponse = UserResponse.builder()
                .id("95b9f607-9884-4c85-96dd-e5ae8d91f1a6")
                .username("hung1")
                .firstName("Hung")
                .lastName("Nguyen")
                .dob(dob)
                .build();

        userEntity = UserEntity.builder()
                .id("95b9f607-9884-4c85-96dd-e5ae8d91f1a6")
                .password("321")
                .username("hung1")
                .firstName("Hung")
                .lastName("Nguyen")
                .dob(dob)
                .build();

        role = Role.builder().name("USER").build();
    }

    @Test
    void createUser_validRequest_success() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(userEntity);
        when(roleRepository.findById(any())).thenReturn(Optional.of(role));

        // WHEN
        var response = userService.createUser(userCreationRequest);

        // THEN
        Assertions.assertThat(response.getId()).isEqualTo("95b9f607-9884-4c85-96dd-e5ae8d91f1a6");
        Assertions.assertThat(response.getUsername()).isEqualTo("hung1");
        Assertions.assertThat(response.getFirstName()).isEqualTo("Hung");
        Assertions.assertThat(response.getLastName()).isEqualTo("Nguyen");
        Assertions.assertThat(response.getDob()).isEqualTo(LocalDate.of(1991, 5, 22));
    }

    @Test
    void createUser_validRequest_success2() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(userEntity);
        when(roleRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN
        var response = userService.createUser(userCreationRequest);

        // THEN
        Assertions.assertThat(response.getId()).isEqualTo("95b9f607-9884-4c85-96dd-e5ae8d91f1a6");
        Assertions.assertThat(response.getUsername()).isEqualTo("hung1");
        Assertions.assertThat(response.getFirstName()).isEqualTo("Hung");
        Assertions.assertThat(response.getLastName()).isEqualTo("Nguyen");
        Assertions.assertThat(response.getDob()).isEqualTo(LocalDate.of(1991, 5, 22));
    }

    @Test
    void createUser_existUser_fail() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // WHEN
        AppException appException = assertThrows(AppException.class, () -> userService.createUser(userCreationRequest));

        // THEN
        Assertions.assertThat(appException.getErrorCode().getCode()).isEqualTo(9001);
    }

    @Test
    void getUsers_success() {
        // GIVEN
        List<UserEntity> mockUser = new ArrayList<>();
        mockUser.add(userEntity);
        when(userRepository.findAll()).thenReturn(mockUser);

        // WHEN
        List<UserResponse> users = userService.getUsers();

        // THEN
        users.forEach(user -> {
            Assertions.assertThat(user.getId()).isEqualTo("95b9f607-9884-4c85-96dd-e5ae8d91f1a6");
            Assertions.assertThat(user.getUsername()).isEqualTo("hung1");
            Assertions.assertThat(user.getFirstName()).isEqualTo("Hung");
            Assertions.assertThat(user.getLastName()).isEqualTo("Nguyen");
            Assertions.assertThat(user.getDob()).isEqualTo(LocalDate.of(1991, 5, 22));
        });
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getUser_adminRole_success() {
        // GIVEN
        when(userRepository.findById(any())).thenReturn(Optional.of(userEntity));

        // WHEN
        userResponse = userService.getUser("95b9f607-9884-4c85-96dd-e5ae8d91f1a6");

        // THEN
        Assertions.assertThat(userResponse.getId()).isEqualTo("95b9f607-9884-4c85-96dd-e5ae8d91f1a6");
        Assertions.assertThat(userResponse.getUsername()).isEqualTo("hung1");
        Assertions.assertThat(userResponse.getFirstName()).isEqualTo("Hung");
        Assertions.assertThat(userResponse.getLastName()).isEqualTo("Nguyen");
        Assertions.assertThat(userResponse.getDob()).isEqualTo(LocalDate.of(1991, 5, 22));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getUser_exitsUser_fail() {
        // GIVEN
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN
        Optional<UserEntity> result = userRepository.findById("95b9f607-9884-4c85-96dd-e5ae8d91f1a6");
        assertFalse(result.isPresent());

        AppException appException =
                assertThrows(AppException.class, () -> userService.getUser("95b9f607-9884-4c85-96dd-e5ae8d91f1a6"));

        // THEN
        Assertions.assertThat(appException.getErrorCode().getCode()).isEqualTo(9002);
    }

    @Test
    @WithMockUser(username = "hung1")
    void getUser_userRole_success() {
        // GIVEN
        when(userRepository.findById(any())).thenReturn(Optional.of(userEntity));

        // WHEN
        userResponse = userService.getUser("95b9f607-9884-4c85-96dd-e5ae8d91f1a6");

        // THEN
        Assertions.assertThat(userResponse.getId()).isEqualTo("95b9f607-9884-4c85-96dd-e5ae8d91f1a6");
        Assertions.assertThat(userResponse.getUsername()).isEqualTo("hung1");
        Assertions.assertThat(userResponse.getFirstName()).isEqualTo("Hung");
        Assertions.assertThat(userResponse.getLastName()).isEqualTo("Nguyen");
        Assertions.assertThat(userResponse.getDob()).isEqualTo(LocalDate.of(1991, 5, 22));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateUser_validRequest_success() {
        // GIVEN
        when(userRepository.findById(anyString())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any())).thenReturn(userEntity);

        // WHEN
        var response = userService.updateUser("95b9f607-9884-4c85-96dd-e5ae8d91f1a6", userUpdateRequest);

        // THEN
        Assertions.assertThat(response.getId()).isEqualTo("95b9f607-9884-4c85-96dd-e5ae8d91f1a6");
        Assertions.assertThat(response.getUsername()).isEqualTo("hung1");
        Assertions.assertThat(response.getFirstName()).isEqualTo("Thu");
        Assertions.assertThat(response.getLastName()).isEqualTo("Pham");
        Assertions.assertThat(response.getDob()).isEqualTo(LocalDate.of(1994, 11, 28));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateUser_exitsUser_fail() {
        // GIVEN
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN
        Optional<UserEntity> result = userRepository.findById("95b9f607-9884-4c85-96dd-e5ae8d91f1a6");
        assertFalse(result.isPresent());

        AppException appException = assertThrows(
                AppException.class,
                () -> userService.updateUser("95b9f607-9884-4c85-96dd-e5ae8d91f1a6", userUpdateRequest));

        // THEN
        Assertions.assertThat(appException.getErrorCode().getCode()).isEqualTo(9002);
    }

    @Test
    @WithMockUser(username = "hung2")
    void getMyInfo_success() {
        // GIVEN
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userEntity));

        // WHEN
        userResponse = userService.getMyInfo();

        // THEN
        Assertions.assertThat(userResponse.getId()).isEqualTo("95b9f607-9884-4c85-96dd-e5ae8d91f1a6");
        Assertions.assertThat(userResponse.getUsername()).isEqualTo("hung1");
        Assertions.assertThat(userResponse.getFirstName()).isEqualTo("Hung");
        Assertions.assertThat(userResponse.getLastName()).isEqualTo("Nguyen");
        Assertions.assertThat(userResponse.getDob()).isEqualTo(LocalDate.of(1991, 5, 22));
    }

    @Test
    @WithMockUser(username = "hung2", roles = "USER")
    void getMyInfo_exitsUser_fail() {
        // GIVEN
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN
        Optional<UserEntity> result = userRepository.findById("95b9f607-9884-4c85-96dd-e5ae8d91f1a6");
        assertFalse(result.isPresent());

        AppException appException = assertThrows(AppException.class, () -> userService.getMyInfo());

        // THEN
        Assertions.assertThat(appException.getErrorCode().getCode()).isEqualTo(9002);
    }
}
