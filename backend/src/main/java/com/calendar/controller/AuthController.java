package com.calendar.controller;

import com.calendar.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/login/google")
    public ResponseEntity<ApiResponse<String>> loginGoogle() {
        try {
            // This will redirect to Google OAuth2 authorization endpoint
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Redirect to Google OAuth2", 
                "/oauth2/authorization/google"));
        } catch (Exception e) {
            log.error("Error initiating Google login", e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "Failed to initiate Google login: " + e.getMessage(), null));
        }
    }

    @GetMapping("/login/outlook")
    public ResponseEntity<ApiResponse<String>> loginOutlook() {
        try {
            // This will redirect to Outlook OAuth2 authorization endpoint
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Redirect to Outlook OAuth2", 
                "/oauth2/authorization/outlook"));
        } catch (Exception e) {
            log.error("Error initiating Outlook login", e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "Failed to initiate Outlook login: " + e.getMessage(), null));
        }
    }

    @GetMapping("/callback/google")
    public ResponseEntity<ApiResponse<Map<String, Object>>> googleCallback() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                OAuth2User principal = oauthToken.getPrincipal();
                
                OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(), 
                    oauthToken.getName()
                );

                Map<String, Object> response = new HashMap<>();
                response.put("provider", "google");
                response.put("user", principal.getAttributes());
                response.put("accessToken", client.getAccessToken().getTokenValue());
                response.put("expiresAt", client.getAccessToken().getExpiresAt());

                log.info("Google OAuth2 authentication successful for user: {}", principal.getName());
                return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Google authentication successful", response));
            }
            
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "Invalid authentication token", null));
        } catch (Exception e) {
            log.error("Error in Google OAuth2 callback", e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "Google OAuth2 callback failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/callback/outlook")
    public ResponseEntity<ApiResponse<Map<String, Object>>> outlookCallback() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                OAuth2User principal = oauthToken.getPrincipal();
                
                OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(), 
                    oauthToken.getName()
                );

                Map<String, Object> response = new HashMap<>();
                response.put("provider", "outlook");
                response.put("user", principal.getAttributes());
                response.put("accessToken", client.getAccessToken().getTokenValue());
                response.put("expiresAt", client.getAccessToken().getExpiresAt());

                log.info("Outlook OAuth2 authentication successful for user: {}", principal.getName());
                return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Outlook authentication successful", response));
            }
            
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "Invalid authentication token", null));
        } catch (Exception e) {
            log.error("Error in Outlook OAuth2 callback", e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "Outlook OAuth2 callback failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAuthStatus() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Map<String, Object> status = new HashMap<>();
            
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                OAuth2User principal = oauthToken.getPrincipal();
                
                OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(), 
                    oauthToken.getName()
                );

                status.put("authenticated", true);
                status.put("provider", oauthToken.getAuthorizedClientRegistrationId());
                status.put("user", principal.getAttributes());
                status.put("accessToken", client.getAccessToken().getTokenValue());
                status.put("expiresAt", client.getAccessToken().getExpiresAt());
            } else {
                status.put("authenticated", false);
            }
            
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Authentication status retrieved", status));
        } catch (Exception e) {
            log.error("Error getting authentication status", e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "Failed to get authentication status: " + e.getMessage(), null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        try {
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Logout successful", null));
        } catch (Exception e) {
            log.error("Error during logout", e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "Logout failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/error")
    public ResponseEntity<ApiResponse<String>> authError() {
        return ResponseEntity.badRequest()
            .body(new ApiResponse<>("ERROR", "Authentication failed", null));
    }
} 