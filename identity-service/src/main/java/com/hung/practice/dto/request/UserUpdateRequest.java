package com.hung.practice.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

import com.hung.practice.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Size(min = 3, message = "PASSWORD_MIN_CHARACTER")
    String password;

    String firstName;

    String lastName;

    @DobConstraint(min = 18, message = "DOB_INVALID")
    LocalDate dob;
}
