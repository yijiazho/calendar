package com.calendar.service;

import com.calendar.model.CalendarEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarService {
    
    private final List<CalendarProvider> calendarProviders;
    
    @Autowired
    public CalendarService(List<CalendarProvider> calendarProviders) {
        this.calendarProviders = calendarProviders;
    }
    
    /**
     * Fetch events from all configured calendar providers
     */
    public List<CalendarEvent> fetchAllEvents(LocalDateTime start, LocalDateTime end) {
        List<CalendarEvent> allEvents = new ArrayList<>();
        
        for (CalendarProvider provider : calendarProviders) {
            if (provider.isConfigured()) {
                try {
                    List<CalendarEvent> events = provider.fetchEvents(start, end);
                    allEvents.addAll(events);
                } catch (Exception e) {
                    // Log error but continue with other providers
                    System.err.println("Error fetching events from " + provider.getProviderName() + ": " + e.getMessage());
                }
            }
        }
        
        return allEvents;
    }
    
    /**
     * Create an event in all configured calendar providers
     */
    public List<CalendarEvent> createEvent(CalendarEvent event) {
        List<CalendarEvent> createdEvents = new ArrayList<>();
        
        for (CalendarProvider provider : calendarProviders) {
            if (provider.isConfigured()) {
                try {
                    CalendarEvent createdEvent = provider.createEvent(event);
                    createdEvents.add(createdEvent);
                } catch (Exception e) {
                    // Log error but continue with other providers
                    System.err.println("Error creating event in " + provider.getProviderName() + ": " + e.getMessage());
                }
            }
        }
        
        return createdEvents;
    }
    
    /**
     * Update an event in all configured calendar providers
     */
    public List<CalendarEvent> updateEvent(CalendarEvent event) {
        List<CalendarEvent> updatedEvents = new ArrayList<>();
        
        for (CalendarProvider provider : calendarProviders) {
            if (provider.isConfigured()) {
                try {
                    CalendarEvent updatedEvent = provider.updateEvent(event);
                    updatedEvents.add(updatedEvent);
                } catch (Exception e) {
                    // Log error but continue with other providers
                    System.err.println("Error updating event in " + provider.getProviderName() + ": " + e.getMessage());
                }
            }
        }
        
        return updatedEvents;
    }
    
    /**
     * Delete an event from all configured calendar providers
     */
    public void deleteEvent(String eventId) {
        for (CalendarProvider provider : calendarProviders) {
            if (provider.isConfigured()) {
                try {
                    provider.deleteEvent(eventId);
                } catch (Exception e) {
                    // Log error but continue with other providers
                    System.err.println("Error deleting event from " + provider.getProviderName() + ": " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Get list of configured calendar providers
     */
    public List<String> getConfiguredProviders() {
        List<String> providers = new ArrayList<>();
        for (CalendarProvider provider : calendarProviders) {
            if (provider.isConfigured()) {
                providers.add(provider.getProviderName());
            }
        }
        return providers;
    }
}
