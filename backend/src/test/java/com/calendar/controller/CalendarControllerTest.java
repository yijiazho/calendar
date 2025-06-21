package com.calendar.controller;

import com.calendar.dto.CalendarEventDto;
import com.calendar.enums.CalendarSource;
import com.calendar.enums.Status;
import com.calendar.model.CalendarEvent;
import com.calendar.service.CalendarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalendarController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
class CalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalendarService calendarService;

    @Autowired
    private ObjectMapper objectMapper;

    private CalendarEventDto sampleDto;
    private CalendarEvent sampleEvent;

    @BeforeEach
    void setUp() {
        sampleDto = new CalendarEventDto();
        sampleDto.setId("1");
        sampleDto.setTitle("Test Event");
        sampleDto.setDescription("Description");
        sampleDto.setLocation("Location");
        sampleDto.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        sampleDto.setEndTime(LocalDateTime.of(2024, 1, 1, 11, 0));
        sampleDto.setAllDay(false);
        sampleDto.setStatus(Status.CONFIRMED);
        sampleDto.setCalendarSource(CalendarSource.GOOGLE);

        sampleEvent = new CalendarEvent();
        sampleEvent.setId("1");
        sampleEvent.setTitle("Test Event");
        sampleEvent.setDescription("Description");
        sampleEvent.setLocation("Location");
        sampleEvent.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        sampleEvent.setEndTime(LocalDateTime.of(2024, 1, 1, 11, 0));
        sampleEvent.setAllDay(false);
        sampleEvent.setStatus(Status.CONFIRMED);
        sampleEvent.setCalendarSource(CalendarSource.GOOGLE);
    }

    @Test
    void testFetchAllEvents() throws Exception {
        when(calendarService.fetchAllEvents(anyString(), any(), any()))
                .thenReturn(List.of(sampleEvent));

        mockMvc.perform(get("/api/calendar/events")
                .param("userId", "user1")
                .param("start", "2024-01-01T00:00:00")
                .param("end", "2024-01-02T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].title").value("Test Event"));
    }

    @Test
    void testCreateEvent() throws Exception {
        when(calendarService.createEvent(anyString(), any(CalendarEvent.class)))
                .thenReturn(List.of(sampleEvent));

        mockMvc.perform(post("/api/calendar/events")
                .param("userId", "user1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].title").value("Test Event"));
    }

    @Test
    void testUpdateEvent() throws Exception {
        when(calendarService.updateEvent(anyString(), any(CalendarEvent.class)))
                .thenReturn(List.of(sampleEvent));

        mockMvc.perform(put("/api/calendar/events")
                .param("userId", "user1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].title").value("Test Event"));
    }

    @Test
    void testDeleteEvent() throws Exception {
        mockMvc.perform(delete("/api/calendar/events/1")
                .param("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Event deleted successfully"));
    }
} 