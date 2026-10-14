package ru.practicum.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventsController {

    List<EventShortDto> getEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size,
            HttpServletRequest request
    );

    EventFullDto getEventById(Long eventId, HttpServletRequest request);
}