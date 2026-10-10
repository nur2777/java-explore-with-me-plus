package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.EwmUtils;
import ru.practicum.events.dao.EventRepository;
import ru.practicum.events.model.State;
import ru.practicum.events.model.Event;
import ru.practicum.exception.ClientErrorException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dao.RequestRepository;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapping.RequestMap;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.RequestStatus;
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
        User user = userCheck(userId);
        Event event = eventCheck(eventId);
        if (userId.equals(event.getInitiator().getId())) {
            throw new ClientErrorException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (event.getState().equals(State.PENDING)) {
            throw new ClientErrorException("Нельзя участвовать в неопубликованном событии.");
        }
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ClientErrorException("Запрос на участие в данном событии уже подан.  Повторный запрос нельзя отправлять.");
        }
        if (event.getParticipantLimit() != null && event.getParticipantLimit() != 0 && requestRepository.countByEventId(eventId) >= event.getParticipantLimit()) {
            throw new ClientErrorException("У события достигнут лимит запросов на участие. Лимит:" + event.getParticipantLimit());
        }
        Request request = new Request();
        request.setEvent(event);
        request.setRequester(user);
        if (event.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return RequestMap.requestToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userCheck(userId);
        EwmUtils.idIsNullCheck(requestId,"Идентификатор заявки requestId");
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Заявка с id " + requestId + " не найдена "));
        request.setStatus(RequestStatus.REJECTED);
        return RequestMap.requestToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public Collection<ParticipationRequestDto> getRequests(Long userId) {
        userCheck(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream().map(RequestMap::requestToParticipationRequestDto).toList();
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEvent(Long eventId) {
        Event event = eventCheck(eventId);
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(RequestMap::requestToParticipationRequestDto).toList();
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByIds(List<Long> requestIds) {
        List<Request> requests = requestRepository.findByIdIn(requestIds);
        return requests.stream().map(RequestMap::requestToParticipationRequestDto).toList();
    }

    /** Метод проверяет идентификатор и существование пользователя
     * @param userId - идентификатор пользователя
     * @return объект пользователя
     */
    private User userCheck(Long userId) {
        EwmUtils.idIsNullCheck(userId,"Идентификатор пользователя userId");
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден "));
    }

    /** Метод проверяет идентификатор и существование события
     * @param eventId - идентификатор события
     * @return - объект события
     */
    private Event eventCheck(Long eventId) {
        EwmUtils.idIsNullCheck(eventId,"Идентификатор события eventId");
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено "));
    }
}
