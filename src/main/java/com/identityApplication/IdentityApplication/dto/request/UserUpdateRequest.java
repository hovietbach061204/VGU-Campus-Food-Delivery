package com.identityApplication.IdentityApplication.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class UserUpdateRequest {
     String username;
     String password;
     String firstname;
     String lastname;
     LocalDate dob;
     List<String> roles;
}
