package ru.practicum.requests.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.Collection;


@RestController
@Slf4j
@RequestMapping("/users/{userId}/requests")
public class RequestControllerImpl implements RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestControllerImpl(RequestService requestService) {
        this.requestService = requestService;
    }


    @PostMapping
    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addNewRequest(@Valid @PathVariable Long userId,
                                                 @RequestParam Long eventId) {
        return requestService.addNewRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @Override
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@Valid @PathVariable Long userId,
                                                 @Valid @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping
    @Override
    public Collection<ParticipationRequestDto> getRequests(@Valid @PathVariable Long userId) {
        return requestService.getRequests(userId);
    }
}
