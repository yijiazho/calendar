package com.calendar.controller;

import com.calendar.dto.ApiResponse;
import com.calendar.dto.CalendarEventDto;
import com.calendar.model.CalendarEvent;
import com.calendar.service.ICalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    private ICalendarService calendarService;

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<CalendarEventDto>>> fetchAllEvents(
            @RequestParam String userId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        try {
            List<CalendarEventDto> events = calendarService.fetchAllEvents(userId, start, end)
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Fetched events successfully", events));
        } catch (Exception ex) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", "Failed to fetch events: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/events")
    public ResponseEntity<ApiResponse<List<CalendarEventDto>>> createEvent(
            @RequestParam String userId,
            @RequestBody CalendarEventDto eventDto) {
        try {
            List<CalendarEventDto> created = calendarService.createEvent(userId, toEntity(eventDto))
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Event(s) created successfully", created));
        } catch (Exception ex) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", "Failed to create event: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/events")
    public ResponseEntity<ApiResponse<List<CalendarEventDto>>> updateEvent(
            @RequestParam String userId,
            @RequestBody CalendarEventDto eventDto) {
        try {
            List<CalendarEventDto> updated = calendarService.updateEvent(userId, toEntity(eventDto))
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Event(s) updated successfully", updated));
        } catch (Exception ex) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", "Failed to update event: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @RequestParam String userId,
            @PathVariable String eventId) {
        try {
            calendarService.deleteEvent(userId, eventId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Event deleted successfully", null));
        } catch (Exception ex) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", "Failed to delete event: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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