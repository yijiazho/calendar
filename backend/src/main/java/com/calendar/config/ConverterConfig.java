package com.calendar.config;

import com.calendar.converter.CalendarEventToGoogleEventConverter;
import com.calendar.converter.CalendarEventToOutlookEventConverter;
import com.calendar.converter.GoogleEventToCalendarEventConverter;
import com.calendar.converter.OutlookEventToCalendarEventConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class ConverterConfig {

    @Bean
    public DefaultConversionService conversionService(
            GoogleEventToCalendarEventConverter googleEventToCalendarEventConverter,
            CalendarEventToGoogleEventConverter calendarEventToGoogleEventConverter,
            OutlookEventToCalendarEventConverter outlookEventToCalendarEventConverter,
            CalendarEventToOutlookEventConverter calendarEventToOutlookEventConverter) {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(googleEventToCalendarEventConverter);
        conversionService.addConverter(calendarEventToGoogleEventConverter);
        conversionService.addConverter(outlookEventToCalendarEventConverter);
        conversionService.addConverter(calendarEventToOutlookEventConverter);
        return conversionService;
    }
} 