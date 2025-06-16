package com.calendar.converter;

import com.calendar.enums.CalendarSource;
import com.calendar.enums.Status;
import com.calendar.model.CalendarEvent;
import com.microsoft.graph.models.Event;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class OutlookEventToCalendarEventConverter implements Converter<Event, CalendarEvent> {
    @Override
    public CalendarEvent convert(Event outlookEvent) {
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
            event.setStatus(Status.valueOf(outlookEvent.showAs.toString()));
        }
        return event;
    }
} 