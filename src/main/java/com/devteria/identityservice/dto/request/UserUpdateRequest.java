package com.devteria.identityservice.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.devteria.identityservice.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    String firstName;
    String lastName;

    @Size(min = 6, message = "INVALID_OLD_PASSWORD")
    String oldPassword;

    @DobConstraint(min = 18, message = "INVALID_DOB")
    LocalDate dob;

    @NotBlank(message = "PHONE_NUMBER_REQUIRED")
    String phoneNumber;
}
