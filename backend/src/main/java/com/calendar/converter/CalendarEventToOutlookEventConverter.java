package com.calendar.converter;

import com.calendar.model.CalendarEvent;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.DateTimeTimeZone;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class CalendarEventToOutlookEventConverter implements Converter<CalendarEvent, Event> {
    @Override
    public Event convert(CalendarEvent event) {
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