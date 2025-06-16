package com.devteria.identityservice.dto.response;

import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserOrderResponse {
    String id;
    String username;
    Set<RoleOrderResponse> roles;
    Double purchaserLat;
    Double purchaserLon;
}
