package com.calendar.service;

import com.calendar.enums.CalendarSource;
import com.calendar.model.CalendarEvent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.OAuth2Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleCalendarProvider implements CalendarProvider {
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    
    @Value("${google.calendar.application-name}")
    private String applicationName;
    
    @Autowired
    private ConversionService conversionService;
    
    protected Calendar getCalendarService(OAuth2AuthorizedClient client) {
        OAuth2Credentials credentials = OAuth2Credentials.create(
            new AccessToken(
                client.getAccessToken().getTokenValue(),
                java.util.Date.from(client.getAccessToken().getExpiresAt())
            )
        );

        return new Calendar.Builder(
            new NetHttpTransport(),
            new GsonFactory(),
            new HttpCredentialsAdapter(credentials))
            .setApplicationName(applicationName)
            .build();
    }
    
    @Override
    public List<CalendarEvent> fetchEvents(OAuth2AuthorizedClient client, LocalDateTime start, LocalDateTime end) {
        try {
            Calendar calendarService = getCalendarService(client);
            List<CalendarEvent> events = new ArrayList<>();
            
            java.util.Date startDate = java.util.Date.from(start.atZone(java.time.ZoneId.systemDefault()).toInstant());
            java.util.Date endDate = java.util.Date.from(end.atZone(java.time.ZoneId.systemDefault()).toInstant());

            com.google.api.client.util.DateTime startDateTime = new com.google.api.client.util.DateTime(startDate);
            com.google.api.client.util.DateTime endDateTime = new com.google.api.client.util.DateTime(endDate);

            com.google.api.services.calendar.model.Events googleEvents = calendarService.events()
                .list("primary")
                .setTimeMin(startDateTime)
                .setTimeMax(endDateTime)
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
    public CalendarEvent createEvent(OAuth2AuthorizedClient client, CalendarEvent event) {
        try {
            Calendar calendarService = getCalendarService(client);
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
    public CalendarEvent updateEvent(OAuth2AuthorizedClient client, CalendarEvent event) {
        try {
            Calendar calendarService = getCalendarService(client);
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
    public void deleteEvent(OAuth2AuthorizedClient client, String eventId) {
        try {
            Calendar calendarService = getCalendarService(client);
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
        return clientId != null && clientSecret != null && applicationName != null;
    }
} 