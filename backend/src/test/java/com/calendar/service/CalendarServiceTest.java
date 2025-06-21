package com.calendar.service;

import com.calendar.model.CalendarEvent;
import com.calendar.enums.CalendarSource;
import com.calendar.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock
    private CalendarProvider googleProvider;

    @Mock
    private CalendarProvider outlookProvider;

    @Mock
    private OAuth2AuthorizedClientManager clientManager;

    @Mock
    private OAuth2AuthorizedClient authorizedClient;

    private CalendarService calendarService;

    @BeforeEach
    void setUp() {
        List<CalendarProvider> providers = List.of(googleProvider, outlookProvider);
        calendarService = new CalendarService(providers, clientManager);
    }

    @Test
    void testStoreAndGetAuthorizedClient() {
        // Given
        String userId = "test-user-123";
        
        // When
        calendarService.storeAuthorizedClient(userId, authorizedClient);
        OAuth2AuthorizedClient retrievedClient = calendarService.getAuthorizedClient(userId);
        
        // Then
        assertNotNull(retrievedClient);
        assertEquals(authorizedClient, retrievedClient);
    }

    @Test
    void testGetAuthorizedClient_NotFound() {
        // Given
        String userId = "non-existent-user";
        
        // When
        OAuth2AuthorizedClient retrievedClient = calendarService.getAuthorizedClient(userId);
        
        // Then
        assertNull(retrievedClient);
    }

    @Test
    void testFetchAllEvents_Success() {
        // Given
        String userId = "test-user-123";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        
        CalendarEvent googleEvent = createTestEvent("google-event-1", "Google Meeting", CalendarSource.GOOGLE);
        CalendarEvent outlookEvent = createTestEvent("outlook-event-1", "Outlook Meeting", CalendarSource.OUTLOOK);
        
        calendarService.storeAuthorizedClient(userId, authorizedClient);
        
        when(googleProvider.isConfigured()).thenReturn(true);
        when(outlookProvider.isConfigured()).thenReturn(true);
        when(googleProvider.fetchEvents(authorizedClient, start, end)).thenReturn(List.of(googleEvent));
        when(outlookProvider.fetchEvents(authorizedClient, start, end)).thenReturn(List.of(outlookEvent));
        
        // When
        List<CalendarEvent> result = calendarService.fetchAllEvents(userId, start, end);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(event -> event.getCalendarSource() == CalendarSource.GOOGLE));
        assertTrue(result.stream().anyMatch(event -> event.getCalendarSource() == CalendarSource.OUTLOOK));
        
        verify(googleProvider).fetchEvents(authorizedClient, start, end);
        verify(outlookProvider).fetchEvents(authorizedClient, start, end);
    }

    @Test
    void testFetchAllEvents_NoAuthorizedClient() {
        // Given
        String userId = "test-user-123";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        
        // When
        List<CalendarEvent> result = calendarService.fetchAllEvents(userId, start, end);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(googleProvider, never()).fetchEvents(any(), any(), any());
        verify(outlookProvider, never()).fetchEvents(any(), any(), any());
    }

    @Test
    void testFetchAllEvents_ProviderNotConfigured() {
        // Given
        String userId = "test-user-123";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        
        calendarService.storeAuthorizedClient(userId, authorizedClient);
        
        when(googleProvider.isConfigured()).thenReturn(false);
        when(outlookProvider.isConfigured()).thenReturn(false);
        
        // When
        List<CalendarEvent> result = calendarService.fetchAllEvents(userId, start, end);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(googleProvider, never()).fetchEvents(any(), any(), any());
        verify(outlookProvider, never()).fetchEvents(any(), any(), any());
    }

    @Test
    void testFetchAllEvents_ProviderThrowsException() {
        // Given
        String userId = "test-user-123";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        
        CalendarEvent outlookEvent = createTestEvent("outlook-event-1", "Outlook Meeting", CalendarSource.OUTLOOK);
        
        calendarService.storeAuthorizedClient(userId, authorizedClient);
        
        when(googleProvider.isConfigured()).thenReturn(true);
        when(outlookProvider.isConfigured()).thenReturn(true);
        when(googleProvider.fetchEvents(authorizedClient, start, end)).thenThrow(new RuntimeException("API Error"));
        when(outlookProvider.fetchEvents(authorizedClient, start, end)).thenReturn(List.of(outlookEvent));
        
        // When
        List<CalendarEvent> result = calendarService.fetchAllEvents(userId, start, end);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CalendarSource.OUTLOOK, result.get(0).getCalendarSource());
        
        verify(googleProvider).fetchEvents(authorizedClient, start, end);
        verify(outlookProvider).fetchEvents(authorizedClient, start, end);
    }

    @Test
    void testCreateEvent_Success() {
        // Given
        String userId = "test-user-123";
        CalendarEvent inputEvent = createTestEvent(null, "New Meeting", null);
        CalendarEvent googleCreatedEvent = createTestEvent("google-event-1", "New Meeting", CalendarSource.GOOGLE);
        CalendarEvent outlookCreatedEvent = createTestEvent("outlook-event-1", "New Meeting", CalendarSource.OUTLOOK);
        
        calendarService.storeAuthorizedClient(userId, authorizedClient);
        
        when(googleProvider.isConfigured()).thenReturn(true);
        when(outlookProvider.isConfigured()).thenReturn(true);
        when(googleProvider.createEvent(authorizedClient, inputEvent)).thenReturn(googleCreatedEvent);
        when(outlookProvider.createEvent(authorizedClient, inputEvent)).thenReturn(outlookCreatedEvent);
        
        // When
        List<CalendarEvent> result = calendarService.createEvent(userId, inputEvent);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(event -> event.getCalendarSource() == CalendarSource.GOOGLE));
        assertTrue(result.stream().anyMatch(event -> event.getCalendarSource() == CalendarSource.OUTLOOK));
        
        verify(googleProvider).createEvent(authorizedClient, inputEvent);
        verify(outlookProvider).createEvent(authorizedClient, inputEvent);
    }

    @Test
    void testCreateEvent_NoAuthorizedClient() {
        // Given
        String userId = "test-user-123";
        CalendarEvent inputEvent = createTestEvent(null, "New Meeting", null);
        
        // When
        List<CalendarEvent> result = calendarService.createEvent(userId, inputEvent);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(googleProvider, never()).createEvent(any(), any());
        verify(outlookProvider, never()).createEvent(any(), any());
    }

    @Test
    void testCreateEvent_ProviderThrowsException() {
        // Given
        String userId = "test-user-123";
        CalendarEvent inputEvent = createTestEvent(null, "New Meeting", null);
        CalendarEvent outlookCreatedEvent = createTestEvent("outlook-event-1", "New Meeting", CalendarSource.OUTLOOK);
        
        calendarService.storeAuthorizedClient(userId, authorizedClient);
        
        when(googleProvider.isConfigured()).thenReturn(true);
        when(outlookProvider.isConfigured()).thenReturn(true);
        when(googleProvider.createEvent(authorizedClient, inputEvent)).thenThrow(new RuntimeException("API Error"));
        when(outlookProvider.createEvent(authorizedClient, inputEvent)).thenReturn(outlookCreatedEvent);
        
        // When
        List<CalendarEvent> result = calendarService.createEvent(userId, inputEvent);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CalendarSource.OUTLOOK, result.get(0).getCalendarSource());
        
        verify(googleProvider).createEvent(authorizedClient, inputEvent);
        verify(outlookProvider).createEvent(authorizedClient, inputEvent);
    }

    @Test
    void testUpdateEvent_Success() {
        // Given
        String userId = "test-user-123";
        CalendarEvent inputEvent = createTestEvent("event-1", "Updated Meeting", null);
        CalendarEvent googleUpdatedEvent = createTestEvent("event-1", "Updated Meeting", CalendarSource.GOOGLE);
        CalendarEvent outlookUpdatedEvent = createTestEvent("event-1", "Updated Meeting", CalendarSource.OUTLOOK);
        
        calendarService.storeAuthorizedClient(userId, authorizedClient);
        
        when(googleProvider.isConfigured()).thenReturn(true);
        when(outlookProvider.isConfigured()).thenReturn(true);
        when(googleProvider.updateEvent(authorizedClient, inputEvent)).thenReturn(googleUpdatedEvent);
        when(outlookProvider.updateEvent(authorizedClient, inputEvent)).thenReturn(outlookUpdatedEvent);
        
        // When
        List<CalendarEvent> result = calendarService.updateEvent(userId, inputEvent);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(event -> event.getCalendarSource() == CalendarSource.GOOGLE));
        assertTrue(result.stream().anyMatch(event -> event.getCalendarSource() == CalendarSource.OUTLOOK));
        
        verify(googleProvider).updateEvent(authorizedClient, inputEvent);
        verify(outlookProvider).updateEvent(authorizedClient, inputEvent);
    }

    @Test
    void testUpdateEvent_NoAuthorizedClient() {
        // Given
        String userId = "test-user-123";
        CalendarEvent inputEvent = createTestEvent("event-1", "Updated Meeting", null);
        
        // When
        List<CalendarEvent> result = calendarService.updateEvent(userId, inputEvent);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(googleProvider, never()).updateEvent(any(), any());
        verify(outlookProvider, never()).updateEvent(any(), any());
    }

    @Test
    void testDeleteEvent_Success() {
        // Given
        String userId = "test-user-123";
        String eventId = "event-1";
        
        calendarService.storeAuthorizedClient(userId, authorizedClient);
        
        when(googleProvider.isConfigured()).thenReturn(true);
        when(outlookProvider.isConfigured()).thenReturn(true);
        
        // When
        calendarService.deleteEvent(userId, eventId);
        
        // Then
        verify(googleProvider).deleteEvent(authorizedClient, eventId);
        verify(outlookProvider).deleteEvent(authorizedClient, eventId);
    }

    @Test
    void testDeleteEvent_NoAuthorizedClient() {
        // Given
        String userId = "test-user-123";
        String eventId = "event-1";
        
        // When
        calendarService.deleteEvent(userId, eventId);
        
        // Then
        verify(googleProvider, never()).deleteEvent(any(), any());
        verify(outlookProvider, never()).deleteEvent(any(), any());
    }

    @Test
    void testDeleteEvent_ProviderThrowsException() {
        // Given
        String userId = "test-user-123";
        String eventId = "event-1";
        
        calendarService.storeAuthorizedClient(userId, authorizedClient);
        
        when(googleProvider.isConfigured()).thenReturn(true);
        when(outlookProvider.isConfigured()).thenReturn(true);
        doThrow(new RuntimeException("API Error")).when(googleProvider).deleteEvent(authorizedClient, eventId);
        
        // When
        calendarService.deleteEvent(userId, eventId);
        
        // Then
        verify(googleProvider).deleteEvent(authorizedClient, eventId);
        verify(outlookProvider).deleteEvent(authorizedClient, eventId);
    }

    @Test
    void testGetConfiguredProviders_AllConfigured() {
        // Given
        when(googleProvider.isConfigured()).thenReturn(true);
        when(outlookProvider.isConfigured()).thenReturn(true);
        when(googleProvider.getProviderName()).thenReturn("Google Calendar");
        when(outlookProvider.getProviderName()).thenReturn("Outlook Calendar");
        
        // When
        List<String> result = calendarService.getConfiguredProviders();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Google Calendar"));
        assertTrue(result.contains("Outlook Calendar"));
    }

    @Test
    void testGetConfiguredProviders_NoneConfigured() {
        // Given
        when(googleProvider.isConfigured()).thenReturn(false);
        when(outlookProvider.isConfigured()).thenReturn(false);
        
        // When
        List<String> result = calendarService.getConfiguredProviders();
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetConfiguredProviders_PartiallyConfigured() {
        // Given
        when(googleProvider.isConfigured()).thenReturn(true);
        when(outlookProvider.isConfigured()).thenReturn(false);
        when(googleProvider.getProviderName()).thenReturn("Google Calendar");
        
        // When
        List<String> result = calendarService.getConfiguredProviders();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains("Google Calendar"));
    }

    private CalendarEvent createTestEvent(String id, String title, CalendarSource source) {
        CalendarEvent event = new CalendarEvent();
        event.setId(id);
        event.setTitle(title);
        event.setDescription("Test description");
        event.setLocation("Test location");
        event.setStartTime(LocalDateTime.now());
        event.setEndTime(LocalDateTime.now().plusHours(1));
        event.setAllDay(false);
        event.setStatus(Status.CONFIRMED);
        event.setCalendarSource(source);
        return event;
    }
} 