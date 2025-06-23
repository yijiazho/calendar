package com.calendar.service;

import com.calendar.config.CalendarConfig;
import com.calendar.enums.CalendarSource;
import com.calendar.model.CalendarEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.security.enabled=true"
})
public class OAuth2IntegrationTest {

    @Autowired
    private GoogleCalendarProvider googleCalendarProvider;

    @Autowired
    private OutlookCalendarProvider outlookCalendarProvider;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private CalendarConfig calendarConfig;

    @Value("${GOOGLE_CLIENT_ID:}")
    private String googleClientId;
    @Value("${GOOGLE_CLIENT_SECRET:}")
    private String googleClientSecret;
    @Value("${GOOGLE_REDIRECT_URI:}")
    private String googleRedirectUri;
    @Value("${GOOGLE_APPLICATION_NAME:}")
    private String googleAppName;
    @Value("${OUTLOOK_CLIENT_ID:}")
    private String outlookClientId;
    @Value("${OUTLOOK_CLIENT_SECRET:}")
    private String outlookClientSecret;
    @Value("${OUTLOOK_REDIRECT_URI:}")
    private String outlookRedirectUri;
    @Value("${OUTLOOK_TENANT_ID:}")
    private String outlookTenantId;

    @Test
    public void printEnv() {
        System.out.println("GOOGLE_CLIENT_ID (Spring): " + googleClientId);
        System.out.println("OUTLOOK_CLIENT_ID (Spring): " + outlookClientId);
    }

    @Test
    public void testGoogleCalendarProviderConfiguration() {
        assertNotNull(googleClientId, "GOOGLE_CLIENT_ID must not be null");
        assertNotNull(googleClientSecret, "GOOGLE_CLIENT_SECRET must not be null");
        assertNotNull(googleRedirectUri, "GOOGLE_REDIRECT_URI must not be null");
        assertNotNull(googleAppName, "GOOGLE_APPLICATION_NAME must not be null");
        assertTrue(googleCalendarProvider.isConfigured(), "Google Calendar Provider should be configured");
        assertEquals(CalendarSource.GOOGLE.name(), googleCalendarProvider.getProviderName());
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        assertNotNull(googleRegistration, "Google OAuth2 client registration should exist");
        assertEquals("google", googleRegistration.getRegistrationId());
        assertNotNull(googleRegistration.getClientId(), "Google client ID should not be null");
        assertNotNull(googleRegistration.getClientSecret(), "Google client secret should not be null");
    }

    @Test
    public void testOutlookCalendarProviderConfiguration() {
        assertNotNull(outlookClientId, "OUTLOOK_CLIENT_ID must not be null");
        assertNotNull(outlookClientSecret, "OUTLOOK_CLIENT_SECRET must not be null");
        assertNotNull(outlookRedirectUri, "OUTLOOK_REDIRECT_URI must not be null");
        assertNotNull(outlookTenantId, "OUTLOOK_TENANT_ID must not be null");
        assertTrue(outlookCalendarProvider.isConfigured(), "Outlook Calendar Provider should be configured");
        assertEquals(CalendarSource.OUTLOOK.name(), outlookCalendarProvider.getProviderName());
        ClientRegistration outlookRegistration = clientRegistrationRepository.findByRegistrationId("outlook");
        assertNotNull(outlookRegistration, "Outlook OAuth2 client registration should exist");
        assertEquals("outlook", outlookRegistration.getRegistrationId());
        assertNotNull(outlookRegistration.getClientId(), "Outlook client ID should not be null");
        assertNotNull(outlookRegistration.getClientSecret(), "Outlook client secret should not be null");
    }

    @Test
    public void testGoogleOAuth2AuthorizationUrl() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        assertNotNull(googleRegistration, "Google OAuth2 client registration should exist");
        String authUrl = googleRegistration.getProviderDetails().getAuthorizationUri();
        assertNotNull(authUrl, "Google authorization URL should not be null");
        assertTrue(authUrl.contains("accounts.google.com"), "Google authorization URL should contain accounts.google.com");
        assertTrue(googleRegistration.getScopes().contains("https://www.googleapis.com/auth/calendar"), "Google scopes should include calendar access");
    }

    @Test
    public void testOutlookOAuth2AuthorizationUrl() {
        ClientRegistration outlookRegistration = clientRegistrationRepository.findByRegistrationId("outlook");
        assertNotNull(outlookRegistration, "Outlook OAuth2 client registration should exist");
        String authUrl = outlookRegistration.getProviderDetails().getAuthorizationUri();
        assertNotNull(authUrl, "Outlook authorization URL should not be null");
        assertTrue(authUrl.contains("login.microsoftonline.com"), "Outlook authorization URL should contain login.microsoftonline.com");
        assertTrue(outlookRegistration.getScopes().contains("Calendars.ReadWrite"), "Outlook scopes should include calendar read/write access");
    }

    @Test
    public void testGoogleCalendarProviderWithoutAuth() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        assertThrows(RuntimeException.class, () -> {
            googleCalendarProvider.fetchEvents(null, start, end);
        }, "Should throw exception when no OAuth2 client is provided");
    }

    @Test
    public void testOutlookCalendarProviderWithoutAuth() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        assertThrows(RuntimeException.class, () -> {
            outlookCalendarProvider.fetchEvents(null, start, end);
        }, "Should throw exception when no OAuth2 client is provided");
    }

    @Test
    public void testCalendarConfig() {
        assertNotNull(calendarConfig, "Calendar configuration should be loaded");
    }

    @Test
    public void testBothProvidersConfigured() {
        assertTrue(googleCalendarProvider.isConfigured(), "Google provider should be configured");
        assertTrue(outlookCalendarProvider.isConfigured(), "Outlook provider should be configured");
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        ClientRegistration outlookRegistration = clientRegistrationRepository.findByRegistrationId("outlook");
        assertNotNull(googleRegistration, "Google OAuth2 client registration should exist");
        assertNotNull(outlookRegistration, "Outlook OAuth2 client registration should exist");
    }
} 