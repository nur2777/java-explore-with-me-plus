package ru.practicum.requests.mapping;


import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.Request;

public class RequestMap {

    public static ParticipationRequestDto requestToParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().name())
                .created(request.getCreated())
                .build();
    }
}
