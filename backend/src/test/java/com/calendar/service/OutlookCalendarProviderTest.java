package com.calendar.service;

import com.calendar.model.CalendarEvent;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.requests.EventCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Answers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutlookCalendarProviderTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GraphServiceClient graphClient;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private OutlookCalendarProvider outlookCalendarProvider;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(outlookCalendarProvider, "graphClient", graphClient);
    }

    @Test
    void fetchEvents_Success() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        Event outlookEvent = new Event();
        EventCollectionPage eventCollectionPage = mock(EventCollectionPage.class);
        CalendarEvent calendarEvent = new CalendarEvent();

        when(graphClient.me().calendarView().buildRequest().filter(anyString()).get())
            .thenReturn(eventCollectionPage);
        when(eventCollectionPage.getCurrentPage()).thenReturn(Arrays.asList(outlookEvent));
        when(conversionService.convert(any(Event.class), eq(CalendarEvent.class)))
            .thenReturn(calendarEvent);

        List<CalendarEvent> result = outlookCalendarProvider.fetchEvents(start, end);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(graphClient.me().calendarView().buildRequest().filter(anyString())).get();
    }

    @Test
    void createEvent_Success() {
        CalendarEvent inputEvent = new CalendarEvent();
        Event outlookEvent = new Event();
        CalendarEvent expectedEvent = new CalendarEvent();

        when(conversionService.convert(inputEvent, Event.class)).thenReturn(outlookEvent);
        when(graphClient.me().events().buildRequest().post(any(Event.class)))
            .thenReturn(outlookEvent);
        when(conversionService.convert(outlookEvent, CalendarEvent.class))
            .thenReturn(expectedEvent);

        CalendarEvent result = outlookCalendarProvider.createEvent(inputEvent);

        assertNotNull(result);
        assertEquals(expectedEvent, result);
        verify(graphClient.me().events().buildRequest()).post(any(Event.class));
    }

    @Test
    void updateEvent_Success() {
        CalendarEvent inputEvent = new CalendarEvent();
        inputEvent.setId("test-id");
        Event outlookEvent = new Event();
        CalendarEvent expectedEvent = new CalendarEvent();

        when(conversionService.convert(inputEvent, Event.class)).thenReturn(outlookEvent);
        when(graphClient.me().events(inputEvent.getId()).buildRequest().patch(any(Event.class)))
            .thenReturn(outlookEvent);
        when(conversionService.convert(outlookEvent, CalendarEvent.class))
            .thenReturn(expectedEvent);

        CalendarEvent result = outlookCalendarProvider.updateEvent(inputEvent);

        assertNotNull(result);
        assertEquals(expectedEvent, result);
        verify(graphClient.me().events(inputEvent.getId()).buildRequest()).patch(any(Event.class));
    }

    @Test
    void deleteEvent_Success() {
        String eventId = "test-id";

        outlookCalendarProvider.deleteEvent(eventId);

        verify(graphClient.me().events(eventId).buildRequest()).delete();
    }

    @Test
    void getProviderName_ReturnsOutlook() {
        assertEquals("OUTLOOK", outlookCalendarProvider.getProviderName());
    }

    @Test
    void isConfigured_ReturnsTrue() {
        assertTrue(outlookCalendarProvider.isConfigured());
    }
} 