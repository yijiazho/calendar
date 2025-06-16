package com.calendar.converter;

import com.calendar.enums.CalendarSource;
import com.calendar.enums.Status;
import com.calendar.model.CalendarEvent;
import com.google.api.services.calendar.model.Event;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class GoogleEventToCalendarEventConverter implements Converter<Event, CalendarEvent> {
    @Override
    public CalendarEvent convert(Event googleEvent) {
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
        
        event.setStatus(Status.valueOf(googleEvent.getStatus()));
        return event;
    }
} 