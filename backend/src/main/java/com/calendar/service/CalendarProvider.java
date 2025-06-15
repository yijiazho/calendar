package com.calendar.service;

import com.calendar.model.CalendarEvent;
import java.time.LocalDateTime;
import java.util.List;

public interface CalendarProvider {
    /**
     * Fetch events from the calendar within the specified time range
     */
    List<CalendarEvent> fetchEvents(LocalDateTime start, LocalDateTime end);
    
    /**
     * Create a new event in the calendar
     */
    CalendarEvent createEvent(CalendarEvent event);
    
    /**
     * Update an existing event in the calendar
     */
    CalendarEvent updateEvent(CalendarEvent event);
    
    /**
     * Delete an event from the calendar
     */
    void deleteEvent(String eventId);
    
    /**
     * Get the provider's name (e.g., "GOOGLE" or "OUTLOOK")
     */
    String getProviderName();
    
    /**
     * Check if the provider is properly configured and authenticated
     */
    boolean isConfigured();
} 