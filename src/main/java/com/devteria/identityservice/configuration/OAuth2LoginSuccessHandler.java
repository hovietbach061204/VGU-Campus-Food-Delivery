package com.devteria.identityservice.configuration;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.service.AuthenticationService;
import com.devteria.identityservice.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String givenName = oauthUser.getAttribute("given_name");
        String familyName = oauthUser.getAttribute("family_name");

        userService.loadOrCreateUser(email, givenName, familyName);

        User hydratedUser = userService.loadUserWithRolesAndPermissions(email);
        String jwt = authenticationService.generateToken(hydratedUser);
        // Redirect to frontend after successful OAuth2 login
        response.sendRedirect("http://localhost:3000?token=" + jwt + "&userId=" + hydratedUser.getId());
    }
}
