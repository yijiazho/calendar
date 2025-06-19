package com.calendar.service;

import com.calendar.model.CalendarEvent;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarProvider {
    /**
     * Fetch events from the calendar provider
     */
    List<CalendarEvent> fetchEvents(OAuth2AuthorizedClient client, LocalDateTime start, LocalDateTime end);
    
    /**
     * Create an event in the calendar provider
     */
    CalendarEvent createEvent(OAuth2AuthorizedClient client, CalendarEvent event);
    
    /**
     * Update an event in the calendar provider
     */
    CalendarEvent updateEvent(OAuth2AuthorizedClient client, CalendarEvent event);
    
    /**
     * Delete an event from the calendar provider
     */
    void deleteEvent(OAuth2AuthorizedClient client, String eventId);
    
    /**
     * Get the name of the calendar provider
     */
    String getProviderName();
    
    /**
     * Check if the calendar provider is configured
     */
    boolean isConfigured();
} 