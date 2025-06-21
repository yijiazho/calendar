package com.calendar.service;

import com.calendar.model.CalendarEvent;
import com.calendar.enums.CalendarSource;
import com.calendar.enums.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "spring.security.enabled", havingValue = "false")
public class MockCalendarService implements ICalendarService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockCalendarService.class);
    
    /**
     * Fetch events from all configured calendar providers for a specific user
     */
    @Override
    public List<CalendarEvent> fetchAllEvents(String userId, LocalDateTime start, LocalDateTime end) {
        logger.info("Mock: Fetching events for user {} from {} to {}", userId, start, end);
        
        List<CalendarEvent> mockEvents = new ArrayList<>();
        
        // Create some mock events
        CalendarEvent event1 = new CalendarEvent();
        event1.setId("mock-1");
        event1.setTitle("Mock Meeting");
        event1.setDescription("This is a mock meeting for testing");
        event1.setLocation("Conference Room A");
        event1.setStartTime(start.plusHours(1));
        event1.setEndTime(start.plusHours(2));
        event1.setAllDay(false);
        event1.setStatus(Status.CONFIRMED);
        event1.setCalendarSource(CalendarSource.GOOGLE);
        mockEvents.add(event1);
        
        CalendarEvent event2 = new CalendarEvent();
        event2.setId("mock-2");
        event2.setTitle("Mock Lunch");
        event2.setDescription("Mock lunch meeting");
        event2.setLocation("Cafeteria");
        event2.setStartTime(start.plusHours(3));
        event2.setEndTime(start.plusHours(4));
        event2.setAllDay(false);
        event2.setStatus(Status.CONFIRMED);
        event2.setCalendarSource(CalendarSource.OUTLOOK);
        mockEvents.add(event2);
        
        return mockEvents;
    }
    
    /**
     * Create an event in all configured calendar providers for a specific user
     */
    @Override
    public List<CalendarEvent> createEvent(String userId, CalendarEvent event) {
        logger.info("Mock: Creating event for user {}: {}", userId, event.getTitle());
        
        // Generate a mock ID
        event.setId("mock-" + UUID.randomUUID().toString().substring(0, 8));
        
        List<CalendarEvent> createdEvents = new ArrayList<>();
        createdEvents.add(event);
        
        return createdEvents;
    }
    
    /**
     * Update an event in all configured calendar providers for a specific user
     */
    @Override
    public List<CalendarEvent> updateEvent(String userId, CalendarEvent event) {
        logger.info("Mock: Updating event for user {}: {}", userId, event.getTitle());
        
        List<CalendarEvent> updatedEvents = new ArrayList<>();
        updatedEvents.add(event);
        
        return updatedEvents;
    }
    
    /**
     * Delete an event from all configured calendar providers for a specific user
     */
    @Override
    public void deleteEvent(String userId, String eventId) {
        logger.info("Mock: Deleting event {} for user {}", eventId, userId);
        // Mock implementation - just log the action
    }
    
    /**
     * Get list of configured calendar providers
     */
    @Override
    public List<String> getConfiguredProviders() {
        List<String> providers = new ArrayList<>();
        providers.add("Google Calendar (Mock)");
        providers.add("Outlook Calendar (Mock)");
        return providers;
    }
} 