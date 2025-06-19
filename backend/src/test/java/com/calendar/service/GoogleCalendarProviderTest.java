package com.calendar.service;

import com.calendar.model.CalendarEvent;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoogleCalendarProviderTest {

    @Mock
    private Calendar calendarService;

    @Mock
    private ConversionService conversionService;

    @Mock
    private OAuth2AuthorizedClient mockOAuth2AuthorizedClient;

    @Spy
    @InjectMocks
    private GoogleCalendarProvider googleCalendarProvider;

    private Calendar.Events mockEvents;
    private Calendar.Events.List mockEventsList;
    private Calendar.Events.Insert mockEventsInsert;
    private Calendar.Events.Update mockEventsUpdate;
    private Calendar.Events.Delete mockEventsDelete;

    @BeforeEach
    void setUp() throws Exception {
        // Setup mock chain for calendar service
        mockEvents = mock(Calendar.Events.class);
        mockEventsList = mock(Calendar.Events.List.class);
        mockEventsInsert = mock(Calendar.Events.Insert.class);
        mockEventsUpdate = mock(Calendar.Events.Update.class);
        mockEventsDelete = mock(Calendar.Events.Delete.class);

        when(calendarService.events()).thenReturn(mockEvents);
        when(mockEvents.list(anyString())).thenReturn(mockEventsList);
        when(mockEvents.insert(anyString(), any(Event.class))).thenReturn(mockEventsInsert);
        when(mockEvents.update(anyString(), anyString(), any(Event.class))).thenReturn(mockEventsUpdate);
        when(mockEvents.delete(anyString(), anyString())).thenReturn(mockEventsDelete);

        // Mock OAuth2AccessToken
        org.springframework.security.oauth2.core.OAuth2AccessToken mockToken = mock(org.springframework.security.oauth2.core.OAuth2AccessToken.class);
        when(mockToken.getTokenValue()).thenReturn("dummy-token");
        when(mockToken.getExpiresAt()).thenReturn(java.time.Instant.now().plusSeconds(3600));
        when(mockOAuth2AuthorizedClient.getAccessToken()).thenReturn(mockToken);

        // Set required fields for isConfigured()
        org.springframework.test.util.ReflectionTestUtils.setField(googleCalendarProvider, "clientId", "dummy");
        org.springframework.test.util.ReflectionTestUtils.setField(googleCalendarProvider, "clientSecret", "dummy");
        org.springframework.test.util.ReflectionTestUtils.setField(googleCalendarProvider, "applicationName", "dummy");

        // Mock getCalendarService to return the mock calendarService
        doReturn(calendarService).when(googleCalendarProvider).getCalendarService(any());
    }

    @Test
    void fetchEvents_Success() throws Exception {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        Event googleEvent = new Event();
        Events events = new Events();
        events.setItems(Arrays.asList(googleEvent));
        CalendarEvent calendarEvent = new CalendarEvent();

        when(mockEventsList.setTimeMin(any())).thenReturn(mockEventsList);
        when(mockEventsList.setTimeMax(any())).thenReturn(mockEventsList);
        when(mockEventsList.setOrderBy(anyString())).thenReturn(mockEventsList);
        when(mockEventsList.setSingleEvents(anyBoolean())).thenReturn(mockEventsList);
        when(mockEventsList.execute()).thenReturn(events);
        when(conversionService.convert(any(Event.class), eq(CalendarEvent.class)))
            .thenReturn(calendarEvent);

        // Act
        List<CalendarEvent> result = googleCalendarProvider.fetchEvents(mockOAuth2AuthorizedClient, start, end);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mockEventsList).setTimeMin(any());
        verify(mockEventsList).setTimeMax(any());
        verify(mockEventsList).setOrderBy("startTime");
        verify(mockEventsList).setSingleEvents(true);
    }

    @Test
    void createEvent_Success() throws Exception {
        // Arrange
        CalendarEvent inputEvent = new CalendarEvent();
        Event googleEvent = new Event();
        CalendarEvent expectedEvent = new CalendarEvent();

        when(conversionService.convert(inputEvent, Event.class)).thenReturn(googleEvent);
        when(mockEventsInsert.execute()).thenReturn(googleEvent);
        when(conversionService.convert(googleEvent, CalendarEvent.class)).thenReturn(expectedEvent);

        // Act
        CalendarEvent result = googleCalendarProvider.createEvent(mockOAuth2AuthorizedClient, inputEvent);

        // Assert
        assertNotNull(result);
        assertEquals(expectedEvent, result);
        verify(mockEventsInsert).execute();
    }

    @Test
    void updateEvent_Success() throws Exception {
        // Arrange
        CalendarEvent inputEvent = new CalendarEvent();
        inputEvent.setId("test-id");
        Event googleEvent = new Event();
        CalendarEvent expectedEvent = new CalendarEvent();

        when(conversionService.convert(inputEvent, Event.class)).thenReturn(googleEvent);
        when(mockEventsUpdate.execute()).thenReturn(googleEvent);
        when(conversionService.convert(googleEvent, CalendarEvent.class)).thenReturn(expectedEvent);

        // Act
        CalendarEvent result = googleCalendarProvider.updateEvent(mockOAuth2AuthorizedClient, inputEvent);

        // Assert
        assertNotNull(result);
        assertEquals(expectedEvent, result);
        verify(mockEventsUpdate).execute();
    }

    @Test
    void deleteEvent_Success() throws Exception {
        // Arrange
        String eventId = "test-id";

        // Act
        googleCalendarProvider.deleteEvent(mockOAuth2AuthorizedClient, eventId);

        // Assert
        verify(mockEventsDelete).execute();
    }

    @Test
    void getProviderName_ReturnsGoogle() {
        assertEquals("GOOGLE", googleCalendarProvider.getProviderName());
    }

    @Test
    void isConfigured_ReturnsTrue() {
        assertTrue(googleCalendarProvider.isConfigured());
    }
} 