package com.hung.practice.dto.response;

import java.time.LocalDate;
import java.util.HashSet;

import com.hung.practice.entity.Role;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String firstName;
    String lastName;
    LocalDate dob;
    HashSet<Role> roles;
}
