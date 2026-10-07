package ru.practicum.events.controller;

import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.NewEventDto;

/**
 * Интерфейс для методов закрытой части работы с событиями
 */
public interface PrivateEventsController {

    /** Метод создания нового события
     * @param userId идентификатор пользователя - создателя события
     * @param newEventDto данные о новом событии
     * @return полное описание созданного события
     */
    EventFullDto addNewEvent(Long userId, NewEventDto newEventDto);
}
