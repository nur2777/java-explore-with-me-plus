package ru.practicum.events.controller;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventUserRequest;

import java.util.List;

/**
 * Интерфейс для методов закрытой части работы с событиями
 */
public interface PrivateEventsController {

    /** Метод формирует список событий, добавленных текущим пользователем
     * @param userId идентификатор пользователя - создателя события
     * @param from количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size количество элементов в наборе
     * @return Список событий
     */
    List<EventShortDto> getEvents(Long userId,
                                  Integer from,
                                  Integer size);

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
