package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.events.dao.EventRepository;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.NewEventDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {

    private final EventRepository eventRepository;

    @Override
    public EventFullDto addNewEvent(Long userId, NewEventDto newEventDto) {
        return null;// TODO продолжить тут
    }
}
