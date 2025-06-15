package com.calendar.service;

import com.calendar.enums.CalendarSource;
import com.calendar.model.CalendarEvent;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.EventCollectionPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OutlookCalendarProvider implements CalendarProvider {
    
    @Value("${outlook.client-id}")
    private String clientId;
    
    @Value("${outlook.client-secret}")
    private String clientSecret;
    
    @Value("${outlook.tenant-id}")
    private String tenantId;
    
    private GraphServiceClient graphClient;
    
    @Override
    public List<CalendarEvent> fetchEvents(LocalDateTime start, LocalDateTime end) {
        try {
            List<CalendarEvent> events = new ArrayList<>();
            EventCollectionPage outlookEvents = graphClient.me().calendarView()
                .buildRequest()
                .filter(String.format("start/dateTime ge '%s' and end/dateTime le '%s'",
                    start.format(DateTimeFormatter.ISO_DATE_TIME),
                    end.format(DateTimeFormatter.ISO_DATE_TIME)))
                .get();
                
            for (Event outlookEvent : outlookEvents.getCurrentPage()) {
                CalendarEvent calendarEvent = convertToCalendarEvent(outlookEvent);
                events.add(calendarEvent);
            }
            
            return events;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch events from Outlook Calendar", e);
        }
    }
    
    @Override
    public CalendarEvent createEvent(CalendarEvent event) {
        try {
            Event outlookEvent = convertToOutlookEvent(event);
            Event createdEvent = graphClient.me().events()
                .buildRequest()
                .post(outlookEvent);
            return convertToCalendarEvent(createdEvent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create event in Outlook Calendar", e);
        }
    }
    
    @Override
    public CalendarEvent updateEvent(CalendarEvent event) {
        try {
            Event outlookEvent = convertToOutlookEvent(event);
            Event updatedEvent = graphClient.me().events(event.getId())
                .buildRequest()
                .patch(outlookEvent);
            return convertToCalendarEvent(updatedEvent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update event in Outlook Calendar", e);
        }
    }
    
    @Override
    public void deleteEvent(String eventId) {
        try {
            graphClient.me().events(eventId)
                .buildRequest()
                .delete();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete event from Outlook Calendar", e);
        }
    }
    
    @Override
    public String getProviderName() {
        return CalendarSource.OUTLOOK.name();
    }
    
    @Override
    public boolean isConfigured() {
        return graphClient != null;
    }
    
    private CalendarEvent convertToCalendarEvent(Event outlookEvent) {
        CalendarEvent event = new CalendarEvent();
        event.setId(outlookEvent.id);
        event.setTitle(outlookEvent.subject);
        event.setDescription(outlookEvent.bodyPreview);
        if (outlookEvent.location != null) {
            event.setLocation(outlookEvent.location.displayName);
        }
        event.setCalendarSource(CalendarSource.OUTLOOK);
        
        if (outlookEvent.start != null && outlookEvent.start.dateTime != null) {
            LocalDateTime startTime = LocalDateTime.parse(outlookEvent.start.dateTime, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime endTime = LocalDateTime.parse(outlookEvent.end.dateTime, DateTimeFormatter.ISO_DATE_TIME);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setAllDay(false);
        } else {
            // Handle all-day events
            event.setAllDay(true);
            // Set start and end times based on date
        }
        
        if (outlookEvent.showAs != null) {
            event.setStatus(outlookEvent.showAs.toString());
        }
        return event;
    }
    
    private Event convertToOutlookEvent(CalendarEvent event) {
        Event outlookEvent = new Event();
        outlookEvent.subject = event.getTitle();
        outlookEvent.bodyPreview = event.getDescription();
        
        // Set start and end times
        DateTimeTimeZone start = new DateTimeTimeZone();
        start.dateTime = event.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME);
        start.timeZone = ZoneId.systemDefault().toString();
        
        DateTimeTimeZone end = new DateTimeTimeZone();
        end.dateTime = event.getEndTime().format(DateTimeFormatter.ISO_DATE_TIME);
        end.timeZone = ZoneId.systemDefault().toString();
        
        outlookEvent.start = start;
        outlookEvent.end = end;
        
        return outlookEvent;
    }
} 