package com.calendar.controller;

import com.calendar.dto.CalendarEventDto;
import com.calendar.enums.CalendarSource;
import com.calendar.enums.Status;
import com.calendar.model.CalendarEvent;
import com.calendar.service.CalendarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalendarController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
class CalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalendarService calendarService;

    @MockBean
    private OAuth2AuthorizedClientService authorizedClientService;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private CalendarEventDto sampleDto;
    private CalendarEvent sampleEvent;
    private OAuth2AuthorizedClient mockClient;
    private OAuth2AuthenticationToken mockAuthToken;

    @BeforeEach
    void setUp() {
        sampleDto = new CalendarEventDto();
        sampleDto.setId("1");
        sampleDto.setTitle("Test Event");
        sampleDto.setDescription("Description");
        sampleDto.setLocation("Location");
        sampleDto.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        sampleDto.setEndTime(LocalDateTime.of(2024, 1, 1, 11, 0));
        sampleDto.setAllDay(false);
        sampleDto.setStatus(Status.CONFIRMED);
        sampleDto.setCalendarSource(CalendarSource.GOOGLE);

        sampleEvent = new CalendarEvent();
        sampleEvent.setId("1");
        sampleEvent.setTitle("Test Event");
        sampleEvent.setDescription("Description");
        sampleEvent.setLocation("Location");
        sampleEvent.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        sampleEvent.setEndTime(LocalDateTime.of(2024, 1, 1, 11, 0));
        sampleEvent.setAllDay(false);
        sampleEvent.setStatus(Status.CONFIRMED);
        sampleEvent.setCalendarSource(CalendarSource.GOOGLE);

        // Mock OAuth2 components
        mockClient = mock(OAuth2AuthorizedClient.class);
        mockAuthToken = mock(OAuth2AuthenticationToken.class);
        
        // Mock access token
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "mock-token",
            Instant.now(),
            Instant.now().plusSeconds(3600)
        );
        when(mockClient.getAccessToken()).thenReturn(accessToken);
        
        // Mock client registration
        ClientRegistration mockRegistration = mock(ClientRegistration.class);
        when(mockRegistration.getRegistrationId()).thenReturn("google");
        when(mockClient.getClientRegistration()).thenReturn(mockRegistration);
        
        // Mock OAuth2 user
        OAuth2User mockUser = mock(OAuth2User.class);
        when(mockUser.getName()).thenReturn("test-user");
        when(mockUser.getAttributes()).thenReturn(Map.of("sub", "test-user-id"));
        when(mockAuthToken.getPrincipal()).thenReturn(mockUser);
        when(mockAuthToken.getName()).thenReturn("test-user");
        when(mockAuthToken.getAuthorizedClientRegistrationId()).thenReturn("google");
        
        // Mock authorized client service
        when(authorizedClientService.loadAuthorizedClient(anyString(), anyString()))
            .thenReturn(mockClient);
            
        // Set up SecurityContext with mock authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(mockAuthToken);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testFetchAllEvents() throws Exception {
        when(calendarService.fetchAllEvents(anyString(), any(), any()))
                .thenReturn(List.of(sampleEvent));

        mockMvc.perform(get("/api/calendar/events")
                .param("start", "2024-01-01T00:00:00")
                .param("end", "2024-01-02T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].title").value("Test Event"));
    }

    @Test
    void testCreateEvent() throws Exception {
        when(calendarService.createEvent(anyString(), any(CalendarEvent.class)))
                .thenReturn(List.of(sampleEvent));

        mockMvc.perform(post("/api/calendar/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].title").value("Test Event"));
    }

    @Test
    void testUpdateEvent() throws Exception {
        when(calendarService.updateEvent(anyString(), any(CalendarEvent.class)))
                .thenReturn(List.of(sampleEvent));

        mockMvc.perform(put("/api/calendar/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].title").value("Test Event"));
    }

    @Test
    void testDeleteEvent() throws Exception {
        mockMvc.perform(delete("/api/calendar/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Event deleted successfully"));
    }

    @Test
    void testGetCalendarStatus() throws Exception {
        when(calendarService.getConfiguredProviders())
            .thenReturn(List.of("google", "outlook"));

        mockMvc.perform(get("/api/calendar/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.authenticated").value(true))
                .andExpect(jsonPath("$.data.provider").value("google"))
                .andExpect(jsonPath("$.data.configuredProviders").isArray());
    }
} 