package com.calendar.controller;

import com.calendar.dto.ApiResponse;
import com.calendar.dto.CalendarEventDto;
import com.calendar.model.CalendarEvent;
import com.calendar.service.CalendarService;
import com.calendar.service.ICalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/calendar")
@Slf4j
public class CalendarController {

    @Autowired
    private ICalendarService calendarService;
    
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<CalendarEventDto>>> fetchAllEvents(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        try {
            OAuth2AuthorizedClient client = getCurrentAuthorizedClient();
            if (client == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("ERROR", "No authenticated OAuth2 client found", null));
            }
            
            // Store the client for the service to use
            String userId = getCurrentUserId();
            ((CalendarService) calendarService).storeAuthorizedClient(userId, client);
            
            List<CalendarEventDto> events = calendarService.fetchAllEvents(userId, start, end)
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Fetched events successfully", events));
        } catch (Exception ex) {
            log.error("Error fetching events", ex);
            return new ResponseEntity<>(new ApiResponse<>("ERROR", "Failed to fetch events: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/events")
    public ResponseEntity<ApiResponse<List<CalendarEventDto>>> createEvent(
            @RequestBody CalendarEventDto eventDto) {
        try {
            OAuth2AuthorizedClient client = getCurrentAuthorizedClient();
            if (client == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("ERROR", "No authenticated OAuth2 client found", null));
            }
            
            String userId = getCurrentUserId();
            ((CalendarService) calendarService).storeAuthorizedClient(userId, client);
            
            List<CalendarEventDto> created = calendarService.createEvent(userId, toEntity(eventDto))
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Event(s) created successfully", created));
        } catch (Exception ex) {
            log.error("Error creating event", ex);
            return new ResponseEntity<>(new ApiResponse<>("ERROR", "Failed to create event: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/events")
    public ResponseEntity<ApiResponse<List<CalendarEventDto>>> updateEvent(
            @RequestBody CalendarEventDto eventDto) {
        try {
            OAuth2AuthorizedClient client = getCurrentAuthorizedClient();
            if (client == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("ERROR", "No authenticated OAuth2 client found", null));
            }
            
            String userId = getCurrentUserId();
            ((CalendarService) calendarService).storeAuthorizedClient(userId, client);
            
            List<CalendarEventDto> updated = calendarService.updateEvent(userId, toEntity(eventDto))
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Event(s) updated successfully", updated));
        } catch (Exception ex) {
            log.error("Error updating event", ex);
            return new ResponseEntity<>(new ApiResponse<>("ERROR", "Failed to update event: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable String eventId) {
        try {
            OAuth2AuthorizedClient client = getCurrentAuthorizedClient();
            if (client == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("ERROR", "No authenticated OAuth2 client found", null));
            }
            
            String userId = getCurrentUserId();
            ((CalendarService) calendarService).storeAuthorizedClient(userId, client);
            
            calendarService.deleteEvent(userId, eventId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Event deleted successfully", null));
        } catch (Exception ex) {
            log.error("Error deleting event", ex);
            return new ResponseEntity<>(new ApiResponse<>("ERROR", "Failed to delete event: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCalendarStatus() {
        try {
            OAuth2AuthorizedClient client = getCurrentAuthorizedClient();
            if (client == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("ERROR", "No authenticated OAuth2 client found", null));
            }
            
            Map<String, Object> status = Map.of(
                "authenticated", true,
                "provider", client.getClientRegistration().getRegistrationId(),
                "configuredProviders", calendarService.getConfiguredProviders(),
                "accessToken", client.getAccessToken().getTokenValue(),
                "expiresAt", client.getAccessToken().getExpiresAt()
            );
            
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Calendar status retrieved", status));
        } catch (Exception ex) {
            log.error("Error getting calendar status", ex);
            return new ResponseEntity<>(new ApiResponse<>("ERROR", "Failed to get calendar status: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private OAuth2AuthorizedClient getCurrentAuthorizedClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            return authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(), 
                oauthToken.getName()
            );
        }
        return null;
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            return oauthToken.getName();
        }
        return "anonymous";
    }

    // Helper methods for mapping between DTO and entity
    private CalendarEventDto toDto(CalendarEvent event) {
        return new CalendarEventDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStartTime(),
                event.getEndTime(),
                event.isAllDay(),
                event.getStatus(),
                event.getCalendarSource()
        );
    }

    private CalendarEvent toEntity(CalendarEventDto dto) {
        CalendarEvent event = new CalendarEvent();
        event.setId(dto.getId());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setAllDay(dto.isAllDay());
        event.setStatus(dto.getStatus());
        event.setCalendarSource(dto.getCalendarSource());
        return event;
    }
} 