package com.calendar.converter;

import com.calendar.model.CalendarEvent;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CalendarEventToGoogleEventConverter implements Converter<CalendarEvent, Event> {
    @Override
    public Event convert(CalendarEvent event) {
        Event googleEvent = new Event();
        googleEvent.setSummary(event.getTitle());
        googleEvent.setDescription(event.getDescription());
        googleEvent.setLocation(event.getLocation());
        
        // Set start and end times
        EventDateTime start = new EventDateTime()
            .setDateTime(com.google.api.client.util.DateTime.parseRfc3339(event.getStartTime().toString()));
        EventDateTime end = new EventDateTime()
            .setDateTime(com.google.api.client.util.DateTime.parseRfc3339(event.getEndTime().toString()));
        
        googleEvent.setStart(start);
        googleEvent.setEnd(end);
        
        return googleEvent;
    }
} 