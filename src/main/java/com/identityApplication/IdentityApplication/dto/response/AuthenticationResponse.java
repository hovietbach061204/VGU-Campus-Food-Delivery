package com.identityApplication.IdentityApplication.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String token; // return the token to user
    boolean authenticated; // if this is true, the user provides the correct password.
}
