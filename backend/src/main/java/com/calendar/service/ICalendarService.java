package com.calendar.service;

import com.calendar.model.CalendarEvent;
import java.time.LocalDateTime;
import java.util.List;

public interface ICalendarService {
    
    /**
     * Fetch events from all configured calendar providers for a specific user
     */
    List<CalendarEvent> fetchAllEvents(String userId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Create an event in all configured calendar providers for a specific user
     */
    List<CalendarEvent> createEvent(String userId, CalendarEvent event);
    
    /**
     * Update an event in all configured calendar providers for a specific user
     */
    List<CalendarEvent> updateEvent(String userId, CalendarEvent event);
    
    /**
     * Delete an event from all configured calendar providers for a specific user
     */
    void deleteEvent(String userId, String eventId);
    
    /**
     * Get list of configured calendar providers
     */
    List<String> getConfiguredProviders();
} 