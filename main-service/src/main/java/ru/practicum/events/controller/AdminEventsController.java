package ru.practicum.events.controller;

import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.UpdateEventAdminRequest;
import ru.practicum.events.model.State;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventsController {

    List<EventFullDto> getEvents(
            List<Long> users,
            List<State> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size
    );

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest request);
}