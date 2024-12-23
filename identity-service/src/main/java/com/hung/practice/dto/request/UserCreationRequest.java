package com.hung.practice.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

import com.hung.practice.validator.DobConstraint;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    String id;

    @Size(min = 4, message = "USERNAME_MIN_CHARACTER")
    String username;

    @Size(min = 3, message = "PASSWORD_MIN_CHARACTER")
    String password;

    String firstName;

    String lastName;

    @DobConstraint(min = 18, message = "DOB_INVALID")
    LocalDate dob;
}
