package com.calendar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.calendar.service.GoogleCalendarProvider;
import com.calendar.service.OutlookCalendarProvider;

@Configuration
public class CalendarConfig {
    
    @Bean
    public GoogleCalendarProvider googleCalendarProvider() {
        return new GoogleCalendarProvider();
    }
    
    @Bean
    public OutlookCalendarProvider outlookCalendarProvider() {
        return new OutlookCalendarProvider();
    }
} 