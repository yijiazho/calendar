package com.calendar.dto;

import com.calendar.enums.CalendarSource;
import com.calendar.enums.Status;
import java.time.LocalDateTime;

public class CalendarEventDto {
    private String id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean allDay;
    private Status status;
    private CalendarSource calendarSource;

    public CalendarEventDto() {}

    public CalendarEventDto(String id, String title, String description, String location,
                            LocalDateTime startTime, LocalDateTime endTime, boolean allDay,
                            Status status, CalendarSource calendarSource) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.allDay = allDay;
        this.status = status;
        this.calendarSource = calendarSource;
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    public String getId() { 
        return id; 
    }

    public void setId(String id) { 
        this.id = id; 
    }

    public String getTitle() { 
        return title; 
    }

    public void setTitle(String title) { 
        this.title = title; 
    }

    public String getDescription() { 
        return description; 
    }

    public void setDescription(String description) { 
        this.description = description; 
    }

    public String getLocation() { 
        return location; 
    }

    public void setLocation(String location) { 
        this.location = location; 
    }

    public LocalDateTime getStartTime() { 
        return startTime; 
    }

    public void setStartTime(LocalDateTime startTime) { 
    this.startTime = startTime;
 }

    public LocalDateTime getEndTime() { 
        return endTime; 
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime; 
    }

    public boolean isAllDay() { 
        return allDay; 
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay; 
    }

    public Status getStatus() { 
        return status; 
    }

    public void setStatus(Status status) { 
        this.status = status; 
    }

    public CalendarSource getCalendarSource() { 
        return calendarSource; 
    }

    public void setCalendarSource(CalendarSource calendarSource) { 
        this.calendarSource = calendarSource; 
    }
}
