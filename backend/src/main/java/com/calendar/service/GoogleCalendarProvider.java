package com.calendar.service;

import com.calendar.enums.CalendarSource;
import com.calendar.model.CalendarEvent;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleCalendarProvider implements CalendarProvider {
    
    @Value("${google.calendar.credentials-file}")
    private String credentialsFilePath;
    
    @Value("${google.calendar.application-name}")
    private String applicationName;
    
    private Calendar calendarService;
    
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
                CalendarEvent calendarEvent = convertToCalendarEvent(event);
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
            Event googleEvent = convertToGoogleEvent(event);
            Event createdEvent = calendarService.events()
                .insert("primary", googleEvent)
                .execute();
            return convertToCalendarEvent(createdEvent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create event in Google Calendar", e);
        }
    }
    
    @Override
    public CalendarEvent updateEvent(CalendarEvent event) {
        try {
            Event googleEvent = convertToGoogleEvent(event);
            Event updatedEvent = calendarService.events()
                .update("primary", event.getId(), googleEvent)
                .execute();
            return convertToCalendarEvent(updatedEvent);
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
    
    private CalendarEvent convertToCalendarEvent(Event googleEvent) {
        CalendarEvent event = new CalendarEvent();
        event.setId(googleEvent.getId());
        event.setTitle(googleEvent.getSummary());
        event.setDescription(googleEvent.getDescription());
        event.setLocation(googleEvent.getLocation());
        event.setCalendarSource(CalendarSource.GOOGLE);
        
        if (googleEvent.getStart().getDateTime() != null) {
            event.setStartTime(LocalDateTime.ofInstant(
                Instant.ofEpochMilli(googleEvent.getStart().getDateTime().getValue()),
                ZoneId.systemDefault()
            ));
            event.setEndTime(LocalDateTime.ofInstant(
                Instant.ofEpochMilli(googleEvent.getEnd().getDateTime().getValue()),
                ZoneId.systemDefault()
            ));
            event.setAllDay(false);
        } else {
            // Handle all-day events
            event.setAllDay(true);
            // Set start and end times based on date
        }
        
        event.setStatus(googleEvent.getStatus());
        return event;
    }
    
    private Event convertToGoogleEvent(CalendarEvent event) {
        Event googleEvent = new Event();
        googleEvent.setSummary(event.getTitle());
        googleEvent.setDescription(event.getDescription());
        googleEvent.setLocation(event.getLocation());
        
        // Set start and end times
        EventDateTime start = new EventDateTime()
            .setDateTime(com.google.api.client.util.DateTime.parseRfc3339(event.getStartTime().toString()));
        EventDateTime end = new EventDateTime()
            .setDateTime(com.google.api.client.util.DateTime.parseRfc3339(event.getEndTime().toString()));
        
        googleEvent.setStart(start);
        googleEvent.setEnd(end);
        
        return googleEvent;
    }
} 