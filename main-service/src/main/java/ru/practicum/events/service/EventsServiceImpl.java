package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.EwmUtils;
import ru.practicum.events.dao.EventRepository;
import ru.practicum.events.dto.*;
import ru.practicum.events.mapping.EventsMap;
import ru.practicum.events.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.users.dao.UserRepository;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventsServiceImpl implements EventsService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public List<EventShortDto> getEvents(Long userId,
                                         Integer from,
                                         Integer size) {
        userCheck(userId);
        Pageable page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId,page);
        return events.stream()
                .map(EventsMap::eventShortDtoFromEvent)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto addNewEvent(Long userId, NewEventDto newEventDto) {
        User initiator = userCheck(userId);
        if (newEventDto == null) {
            throw new ValidationException("Данные нового события должны быть заполнены!");
        }
        if (newEventDto.getEventDate() != null && newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время на которые намечено событие (" + newEventDto.getEventDate().format(dateTimeFormatter) + ") не может быть раньше, " +
                    "чем через два часа от текущего момента (" + LocalDateTime.now().plusHours(2).format(dateTimeFormatter) + ")");
        }
        checkNegativeLimit(newEventDto.getParticipantLimit());
        Event newEvent = EventsMap.newEventDtoToEvent(newEventDto);
        newEvent.setInitiator(initiator);
        newEvent.setState(State.PENDING.name());
        Event createdEvent = eventRepository.save(newEvent);
        //TODO необходимо заполнять confirmedRequests и views
        return EventsMap.eventFullDtoFromEvent(createdEvent);
    }

    @Override
    @Transactional
    public EventFullDto userUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        userCheck(userId);
        Event event = eventCheck(eventId);
        if (updateEventUserRequest == null) {
            throw new ValidationException("Данные об изменении в событии должны быть заполнены!");
        }
        if (!Objects.equals(userId,event.getInitiator().getId())) {
            throw new ValidationException("Пользователь выполняющий изменение не является инициатором события. Редактировать событие может только его инициатор.");
        }
        if (updateEventUserRequest.getEventDate() != null && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата и время на которые намечено событие (" + updateEventUserRequest.getEventDate().format(dateTimeFormatter)
                    + ") не может быть раньше текущего момента (" + LocalDateTime.now().format(dateTimeFormatter) + ")");
        }
        checkNegativeLimit(updateEventUserRequest.getParticipantLimit());
        EventsMap.updateEventUserRequestToEvent(updateEventUserRequest,event);
        Event updatedEvent = eventRepository.save(event);
        //TODO необходимо заполнять confirmedRequests и views
        return EventsMap.eventFullDtoFromEvent(updatedEvent);
    }

    /** Метод проверяет идентификатор и существование пользователя
     * @param userId - идентификатор пользователя
     * @return объект пользователя
     */
    private User userCheck(Long userId) {
        EwmUtils.idIsNullCheck(userId,"Идентификатор пользователя userId");
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден "));
    }

    /** Метод проверяет корректность указания лимита участников
     * @param limit - лимит
     */
    private static void checkNegativeLimit(Integer limit) {
        if (limit != null && limit < 0) {
            throw new ValidationException("Лимит участников не может быть отрицательным");
        }
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
