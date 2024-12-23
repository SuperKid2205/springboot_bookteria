package com.hung.practice.configuration;

import java.util.*;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.hung.practice.entity.Permission;
import com.hung.practice.entity.Role;
import com.hung.practice.entity.UserEntity;
import com.hung.practice.enums.ErrorCode;
import com.hung.practice.enums.UserRoles;
import com.hung.practice.exception.AppException;
import com.hung.practice.repository.PermissionRepository;
import com.hung.practice.repository.RoleRepository;
import com.hung.practice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional
public class ApplicationInitializationConfig {

    final PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    @Order(1)
    ApplicationRunner runnerForPermissionCreation(PermissionRepository permissionRepository) {
        return args -> {

            // Create permission if not exited.
            if (permissionRepository.findAll().isEmpty()) {

                List<Permission> permissionList = Arrays.asList(
                        Permission.builder()
                                .name("CREATE_POST")
                                .description("This user is allowed to create a post.")
                                .build(),
                        Permission.builder()
                                .name("UPDATE_POST")
                                .description("This user is allowed to update a post.")
                                .build(),
                        Permission.builder()
                                .name("APPROVE_POST")
                                .description("This user is allowed to approve a post.")
                                .build(),
                        Permission.builder()
                                .name("REJECT_POST")
                                .description("This user is allowed to reject a post.")
                                .build());

                permissionRepository.saveAll(permissionList);
                log.info("Permission was created");
            }
        };
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    @Order(2)
    ApplicationRunner runnerForAdminRoleCreation(
            RoleRepository roleRepository, PermissionRepository permissionRepository) {
        return args -> {
            if (!roleRepository.existsById(UserRoles.ADMIN.name())) {

                List<Permission> permissions = new ArrayList<>();
                if (!permissionRepository.findAll().isEmpty()) {
                    permissions = permissionRepository.findAll();
                }

                Role role = Role.builder()
                        .name(UserRoles.ADMIN.name())
                        .description("Admin User")
                        .permissions(new HashSet<>(permissions))
                        .build();

                roleRepository.save(role);
                log.info("Role was created");
            }

            if (!roleRepository.existsById("USER")) {

                Set<Permission> permissions = new HashSet<>();
                Optional<Permission> updatePermission = permissionRepository.findById("UPDATE_POST");
                updatePermission.ifPresent(permissions::add);

                Role role = Role.builder()
                        .name("USER")
                        .description("Normal User")
                        .permissions(permissions)
                        .build();

                roleRepository.save(role);
                log.info("Role was created");
            }
        };
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    @Order(3)
    ApplicationRunner runnerForAdminUserCreation(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {

            // Create admin user if not exited.
            if (userRepository.findByUsername("admin").isEmpty()) {

                HashSet<Role> roles = new HashSet<>();
                Role adminRole = roleRepository
                        .findById(UserRoles.ADMIN.name())
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXITS));

                roles.add(adminRole);

                UserEntity adminUser = UserEntity.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("321"))
                        .roles(roles)
                        .build();

                userRepository.save(adminUser);
                log.info("User admin created with default info.");
            }
        };
    }
}
