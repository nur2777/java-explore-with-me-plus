package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.dao.EventRepository;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.State;
import ru.practicum.events.mapping.EventsMap;
import ru.practicum.events.model.Event;
import ru.practicum.exception.ValidationException;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventsServiceImpl implements EventsService {

    private final EventRepository eventRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    @Transactional
    public EventFullDto addNewEvent(Long userId, NewEventDto newEventDto) {
        if (userId == null) {
            throw new ValidationException("Идентификатор пользователя userId должен быть заполнен!");
        }
        User initiator = new User();
        initiator.setId(userId);
        if (newEventDto == null) {
            throw new ValidationException("Данные нового пользователя должны быть заполнены!");
        }
        if (newEventDto.getEventDate() != null && newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время на которые намечено событие (" +
                    newEventDto.getEventDate().format(dateTimeFormatter) +") не может быть раньше, " +
                    "чем через два часа от текущего момента (" +
                    LocalDateTime.now().plusHours(2).format(dateTimeFormatter)+ ")");
        }
        if (newEventDto.getParticipantLimit() < 0 ) {
            throw new ValidationException("Лимит участников не может быть отрицательным");
        }
        Event newEvent = EventsMap.newEventDtoToEvent(newEventDto);
        newEvent.setInitiator(initiator);
        newEvent.setState(State.PENDING.name());
        Event createdEvent = eventRepository.save(newEvent);
        return EventsMap.eventFullDtoFromEvent(createdEvent);
    }
}
