package com.calendar.service;

import com.calendar.enums.CalendarSource;
import com.calendar.model.CalendarEvent;
import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.requests.EventCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OutlookCalendarProvider implements CalendarProvider {
    
    @Value("${spring.security.oauth2.client.registration.outlook.client-id}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.registration.outlook.client-secret}")
    private String clientSecret;
    
    @Value("${outlook.tenant-id}")
    private String tenantId;
    
    @Autowired
    private ConversionService conversionService;
    
    protected GraphServiceClient<?> getGraphClient(OAuth2AuthorizedClient client) {
        TokenCredential tokenCredential = new TokenCredential() {
            public Mono<AccessToken> getToken(com.azure.core.credential.TokenRequestContext request) {
                return Mono.just(new AccessToken(
                    client.getAccessToken().getTokenValue(),
                    client.getAccessToken().getExpiresAt().atOffset(ZoneOffset.UTC)
                ));
            }
        };
        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(tokenCredential);
        return GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
    }
    
    @Override
    public List<CalendarEvent> fetchEvents(OAuth2AuthorizedClient client, LocalDateTime start, LocalDateTime end) {
        try {
            GraphServiceClient<?> graphClient = getGraphClient(client);
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
    public CalendarEvent createEvent(OAuth2AuthorizedClient client, CalendarEvent event) {
        try {
            GraphServiceClient<?> graphClient = getGraphClient(client);
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
    public CalendarEvent updateEvent(OAuth2AuthorizedClient client, CalendarEvent event) {
        try {
            GraphServiceClient<?> graphClient = getGraphClient(client);
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
    public void deleteEvent(OAuth2AuthorizedClient client, String eventId) {
        try {
            GraphServiceClient<?> graphClient = getGraphClient(client);
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
        return clientId != null && clientSecret != null && tenantId != null;
    }
} 