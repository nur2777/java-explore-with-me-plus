package ru.practicum.events.controller;

import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventUserRequest;

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


    /** Метод изменения события добавленного текущим пользователем
     * @param userId идентификатор пользователя - изменяющего событие
     * @param eventId идентификатор события
     * @param updateEventUserRequest измененные данные в событии
     * @return полное описание созданного события
     */
    EventFullDto userUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

}
