package com.calendar.service;

import com.calendar.model.CalendarEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CalendarService {
    
    private static final Logger logger = LoggerFactory.getLogger(CalendarService.class);
    private final List<CalendarProvider> calendarProviders;
    private final OAuth2AuthorizedClientManager clientManager;
    private final Map<String, OAuth2AuthorizedClient> authorizedClients = new ConcurrentHashMap<>();
    
    @Autowired
    public CalendarService(List<CalendarProvider> calendarProviders, 
                         OAuth2AuthorizedClientManager clientManager) {
        this.calendarProviders = calendarProviders;
        this.clientManager = clientManager;
    }
    
    /**
     * Store authorized client for a user
     */
    public void storeAuthorizedClient(String userId, OAuth2AuthorizedClient client) {
        authorizedClients.put(userId, client);
    }
    
    /**
     * Get authorized client for a user
     */
    public OAuth2AuthorizedClient getAuthorizedClient(String userId) {
        return authorizedClients.get(userId);
    }
    
    /**
     * Fetch events from all configured calendar providers for a specific user
     */
    public List<CalendarEvent> fetchAllEvents(String userId, LocalDateTime start, LocalDateTime end) {
        List<CalendarEvent> allEvents = new ArrayList<>();
        OAuth2AuthorizedClient client = getAuthorizedClient(userId);
        
        if (client == null) {
            logger.warn("No authorized client found for user: {}", userId);
            return allEvents;
        }
        
        for (CalendarProvider provider : calendarProviders) {
            if (provider.isConfigured()) {
                try {
                    List<CalendarEvent> events = provider.fetchEvents(client, start, end);
                    allEvents.addAll(events);
                } catch (Exception e) {
                    logger.error("Error fetching events from {} for user {}: {}", 
                        provider.getProviderName(), userId, e.getMessage());
                }
            }
        }
        
        return allEvents;
    }
    
    /**
     * Create an event in all configured calendar providers for a specific user
     */
    public List<CalendarEvent> createEvent(String userId, CalendarEvent event) {
        List<CalendarEvent> createdEvents = new ArrayList<>();
        OAuth2AuthorizedClient client = getAuthorizedClient(userId);
        
        if (client == null) {
            logger.warn("No authorized client found for user: {}", userId);
            return createdEvents;
        }
        
        for (CalendarProvider provider : calendarProviders) {
            if (provider.isConfigured()) {
                try {
                    CalendarEvent createdEvent = provider.createEvent(client, event);
                    createdEvents.add(createdEvent);
                } catch (Exception e) {
                    logger.error("Error creating event in {} for user {}: {}", 
                        provider.getProviderName(), userId, e.getMessage());
                }
            }
        }
        
        return createdEvents;
    }
    
    /**
     * Update an event in all configured calendar providers for a specific user
     */
    public List<CalendarEvent> updateEvent(String userId, CalendarEvent event) {
        List<CalendarEvent> updatedEvents = new ArrayList<>();
        OAuth2AuthorizedClient client = getAuthorizedClient(userId);
        
        if (client == null) {
            logger.warn("No authorized client found for user: {}", userId);
            return updatedEvents;
        }
        
        for (CalendarProvider provider : calendarProviders) {
            if (provider.isConfigured()) {
                try {
                    CalendarEvent updatedEvent = provider.updateEvent(client, event);
                    updatedEvents.add(updatedEvent);
                } catch (Exception e) {
                    logger.error("Error updating event in {} for user {}: {}", 
                        provider.getProviderName(), userId, e.getMessage());
                }
            }
        }
        
        return updatedEvents;
    }
    
    /**
     * Delete an event from all configured calendar providers for a specific user
     */
    public void deleteEvent(String userId, String eventId) {
        OAuth2AuthorizedClient client = getAuthorizedClient(userId);
        
        if (client == null) {
            logger.warn("No authorized client found for user: {}", userId);
            return;
        }
        
        for (CalendarProvider provider : calendarProviders) {
            if (provider.isConfigured()) {
                try {
                    provider.deleteEvent(client, eventId);
                } catch (Exception e) {
                    logger.error("Error deleting event from {} for user {}: {}", 
                        provider.getProviderName(), userId, e.getMessage());
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
