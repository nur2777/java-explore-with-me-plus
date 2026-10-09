package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.EwmUtils;
import ru.practicum.events.dao.EventRepository;
import ru.practicum.events.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dao.RequestRepository;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapping.RequestMap;
import ru.practicum.requests.model.Request;
import ru.practicum.users.dao.UserRepository;
import ru.practicum.users.model.User;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto addNewRequest(Long userId, Long eventId) {
        EwmUtils.idIsNullCheck(userId,"Идентификатор пользователя userId");
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден "));
        EwmUtils.idIsNullCheck(eventId,"Идентификатор события eventId");
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено "));
        Request request = new Request();
        request.setEvent(event);
        request.setRequester(user);
        request.setStatus("PENDING");
        return RequestMap.requestToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        EwmUtils.idIsNullCheck(userId,"Идентификатор пользователя userId");
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден "));
        EwmUtils.idIsNullCheck(requestId,"Идентификатор заявки requestId");
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Заяявка с id " + requestId + " не найдена "));
        request.setStatus("CANCELED");
        return RequestMap.requestToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public Collection<ParticipationRequestDto> getRequests(Long userId) {
        EwmUtils.idIsNullCheck(userId,"Идентификатор пользователя userId");
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден "));
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream().map(RequestMap::requestToParticipationRequestDto).toList();
    }
}
