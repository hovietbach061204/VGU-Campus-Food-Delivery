package com.identityApplication.IdentityApplication.controller;

import com.identityApplication.IdentityApplication.dto.RefreshRequest;
import com.identityApplication.IdentityApplication.dto.request.APIResponse;
import com.identityApplication.IdentityApplication.dto.request.AuthenticationRequest;
import com.identityApplication.IdentityApplication.dto.request.IntrospectRequest;
import com.identityApplication.IdentityApplication.dto.request.LogoutRequest;
import com.identityApplication.IdentityApplication.dto.response.AuthenticationResponse;
import com.identityApplication.IdentityApplication.dto.response.IntrospectResponse;
import com.identityApplication.IdentityApplication.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor // Autowired Bean
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    APIResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return APIResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/introspect")
    APIResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return APIResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    APIResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return APIResponse.<Void>builder().build();
    }

    @PostMapping("/refresh")
    APIResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return APIResponse.<AuthenticationResponse>builder().result(result).build();
    }

}
