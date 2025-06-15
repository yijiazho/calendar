package com.calendar.model;

import com.calendar.enums.CalendarSource;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CalendarEvent {
    private String id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean allDay;
    private String status;
    private CalendarSource calendarSource;
}
