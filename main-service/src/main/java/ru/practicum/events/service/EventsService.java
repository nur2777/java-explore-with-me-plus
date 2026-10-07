package ru.practicum.events.service;

import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.NewEventDto;

/**
 * Интерфейс реализует логику операций для событий
 */
public interface EventsService {

    /** Метод создания нового события
     * @param userId идентификатор пользователя - создателя события
     * @param newEventDto данные о новом событии
     * @return полное описание созданного события
     */
    EventFullDto addNewEvent(Long userId, NewEventDto newEventDto);
}
