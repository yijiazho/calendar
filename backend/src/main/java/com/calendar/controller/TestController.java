package com.calendar.controller;

import com.calendar.dto.ApiResponse;
import com.calendar.service.GoogleCalendarProvider;
import com.calendar.service.OutlookCalendarProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private GoogleCalendarProvider googleCalendarProvider;

    @Autowired
    private OutlookCalendarProvider outlookCalendarProvider;

    @GetMapping("/oauth2-config")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOAuth2Config() {
        try {
            Map<String, Object> config = new HashMap<>();
            
            // Test Google OAuth2 configuration
            ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
            if (googleRegistration != null) {
                Map<String, Object> googleConfig = new HashMap<>();
                googleConfig.put("clientId", googleRegistration.getClientId());
                googleConfig.put("clientSecret", googleRegistration.getClientSecret() != null ? "***SET***" : "***NOT SET***");
                googleConfig.put("redirectUri", googleRegistration.getRedirectUri());
                googleConfig.put("scopes", googleRegistration.getScopes());
                googleConfig.put("authorizationUri", googleRegistration.getProviderDetails().getAuthorizationUri());
                googleConfig.put("tokenUri", googleRegistration.getProviderDetails().getTokenUri());
                config.put("google", googleConfig);
            } else {
                config.put("google", "NOT_CONFIGURED");
            }
            
            // Test Outlook OAuth2 configuration
            ClientRegistration outlookRegistration = clientRegistrationRepository.findByRegistrationId("outlook");
            if (outlookRegistration != null) {
                Map<String, Object> outlookConfig = new HashMap<>();
                outlookConfig.put("clientId", outlookRegistration.getClientId());
                outlookConfig.put("clientSecret", outlookRegistration.getClientSecret() != null ? "***SET***" : "***NOT SET***");
                outlookConfig.put("redirectUri", outlookRegistration.getRedirectUri());
                outlookConfig.put("scopes", outlookRegistration.getScopes());
                outlookConfig.put("authorizationUri", outlookRegistration.getProviderDetails().getAuthorizationUri());
                outlookConfig.put("tokenUri", outlookRegistration.getProviderDetails().getTokenUri());
                config.put("outlook", outlookConfig);
            } else {
                config.put("outlook", "NOT_CONFIGURED");
            }
            
            // Test provider configuration
            Map<String, Object> providers = new HashMap<>();
            providers.put("googleConfigured", googleCalendarProvider.isConfigured());
            providers.put("outlookConfigured", outlookCalendarProvider.isConfigured());
            config.put("providers", providers);
            
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "OAuth2 configuration retrieved", config));
        } catch (Exception e) {
            log.error("Error getting OAuth2 configuration", e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "Failed to get OAuth2 configuration: " + e.getMessage(), null));
        }
    }

    @GetMapping("/auth-urls")
    public ResponseEntity<ApiResponse<Map<String, String>>> getAuthUrls() {
        try {
            Map<String, String> authUrls = new HashMap<>();
            
            // Google OAuth2 authorization URL
            ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
            if (googleRegistration != null) {
                String googleAuthUrl = googleRegistration.getProviderDetails().getAuthorizationUri() +
                    "?client_id=" + googleRegistration.getClientId() +
                    "&redirect_uri=" + googleRegistration.getRedirectUri() +
                    "&scope=" + String.join(" ", googleRegistration.getScopes()) +
                    "&response_type=code";
                authUrls.put("google", googleAuthUrl);
            }
            
            // Outlook OAuth2 authorization URL
            ClientRegistration outlookRegistration = clientRegistrationRepository.findByRegistrationId("outlook");
            if (outlookRegistration != null) {
                String outlookAuthUrl = outlookRegistration.getProviderDetails().getAuthorizationUri() +
                    "?client_id=" + outlookRegistration.getClientId() +
                    "&redirect_uri=" + outlookRegistration.getRedirectUri() +
                    "&scope=" + String.join(" ", outlookRegistration.getScopes()) +
                    "&response_type=code";
                authUrls.put("outlook", outlookAuthUrl);
            }
            
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Authorization URLs generated", authUrls));
        } catch (Exception e) {
            log.error("Error generating authorization URLs", e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "Failed to generate authorization URLs: " + e.getMessage(), null));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "UP");
            health.put("timestamp", System.currentTimeMillis());
            health.put("googleProviderConfigured", googleCalendarProvider.isConfigured());
            health.put("outlookProviderConfigured", outlookCalendarProvider.isConfigured());
            
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Health check passed", health));
        } catch (Exception e) {
            log.error("Health check failed", e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "Health check failed: " + e.getMessage(), null));
        }
    }
} 