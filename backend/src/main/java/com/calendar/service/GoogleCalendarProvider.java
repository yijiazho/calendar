package com.calendar.service;

import com.calendar.enums.CalendarSource;
import com.calendar.model.CalendarEvent;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleCalendarProvider implements CalendarProvider {
    
    @Value("${google.calendar.credentials-file}")
    private String credentialsFilePath;
    
    @Value("${google.calendar.application-name}")
    private String applicationName;
    
    private Calendar calendarService;
    
    @Autowired
    private ConversionService conversionService;
    
    @Override
    public List<CalendarEvent> fetchEvents(LocalDateTime start, LocalDateTime end) {
        try {
            List<CalendarEvent> events = new ArrayList<>();
            com.google.api.services.calendar.model.Events googleEvents = calendarService.events()
                .list("primary")
                .setTimeMin(com.google.api.client.util.DateTime.parseRfc3339(start.toString()))
                .setTimeMax(com.google.api.client.util.DateTime.parseRfc3339(end.toString()))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
                
            for (Event event : googleEvents.getItems()) {
                CalendarEvent calendarEvent = conversionService.convert(event, CalendarEvent.class);
                events.add(calendarEvent);
            }
            
            return events;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch events from Google Calendar", e);
        }
    }
    
    @Override
    public CalendarEvent createEvent(CalendarEvent event) {
        try {
            Event googleEvent = conversionService.convert(event, Event.class);
            Event createdEvent = calendarService.events()
                .insert("primary", googleEvent)
                .execute();
            return conversionService.convert(createdEvent, CalendarEvent.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create event in Google Calendar", e);
        }
    }
    
    @Override
    public CalendarEvent updateEvent(CalendarEvent event) {
        try {
            Event googleEvent = conversionService.convert(event, Event.class);
            Event updatedEvent = calendarService.events()
                .update("primary", event.getId(), googleEvent)
                .execute();
            return conversionService.convert(updatedEvent, CalendarEvent.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update event in Google Calendar", e);
        }
    }
    
    @Override
    public void deleteEvent(String eventId) {
        try {
            calendarService.events().delete("primary", eventId).execute();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete event from Google Calendar", e);
        }
    }
    
    @Override
    public String getProviderName() {
        return CalendarSource.GOOGLE.name();
    }
    
    @Override
    public boolean isConfigured() {
        return calendarService != null;
    }
} 