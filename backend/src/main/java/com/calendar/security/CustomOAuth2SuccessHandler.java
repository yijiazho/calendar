package com.calendar.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String redirectUrl = "/";
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String registrationId = oauthToken.getAuthorizedClientRegistrationId();
            // You can customize the redirect URL based on the provider
            if ("google".equals(registrationId) || "outlook".equals(registrationId)) {
                redirectUrl = "/auth/callback/" + registrationId;
            } 
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect("auth/error");
        }
        

    }
}
