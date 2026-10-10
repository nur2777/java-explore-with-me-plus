package ru.practicum.events.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.*;
import ru.practicum.events.service.EventsService;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/events")
public class PrivateEventsControllerImpl implements PrivateEventsController {

    private final EventsService eventsService;

    @Autowired
    public PrivateEventsControllerImpl(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @GetMapping
    @Override
    public List<EventShortDto> getEvents(@PathVariable Long userId,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        return eventsService.getEvents(userId, from, size);
    }

    @PostMapping
    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addNewEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        return eventsService.addNewEvent(userId,newEventDto);
    }

    @GetMapping("/{eventId}")
    @Override
    public EventFullDto getOneEvent(Long userId, Long eventId) {
        return eventsService.getOneEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @Override
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto userUpdateEvent(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventsService.userUpdateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    @Override
    @ResponseStatus(HttpStatus.OK)
    public Collection<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                                                                @PathVariable Long eventId) {
        return eventsService.getEventRequests(userId, eventId);
    }


    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public EventRequestStatusUpdateResult userConfirmRejectRequest(@PathVariable Long userId,
                                                                   @PathVariable Long eventId,
                                                                   @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return eventsService.userConfirmRejectRequest(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
