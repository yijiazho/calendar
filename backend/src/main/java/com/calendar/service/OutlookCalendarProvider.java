package com.calendar.service;

import com.calendar.enums.CalendarSource;
import com.calendar.model.CalendarEvent;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.EventCollectionPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    
    @Autowired
    private ConversionService conversionService;
    
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
                CalendarEvent calendarEvent = conversionService.convert(outlookEvent, CalendarEvent.class);
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
            Event outlookEvent = conversionService.convert(event, Event.class);
            Event createdEvent = graphClient.me().events()
                .buildRequest()
                .post(outlookEvent);
            return conversionService.convert(createdEvent, CalendarEvent.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create event in Outlook Calendar", e);
        }
    }
    
    @Override
    public CalendarEvent updateEvent(CalendarEvent event) {
        try {
            Event outlookEvent = conversionService.convert(event, Event.class);
            Event updatedEvent = graphClient.me().events(event.getId())
                .buildRequest()
                .patch(outlookEvent);
            return conversionService.convert(updatedEvent, CalendarEvent.class);
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
} 