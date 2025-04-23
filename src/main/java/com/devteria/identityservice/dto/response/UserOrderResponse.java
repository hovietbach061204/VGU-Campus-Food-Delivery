package com.devteria.identityservice.dto.response;

import com.devteria.identityservice.status.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserOrderResponse {
    String id;
    String username;
    Set<RoleResponse> roles;
}
