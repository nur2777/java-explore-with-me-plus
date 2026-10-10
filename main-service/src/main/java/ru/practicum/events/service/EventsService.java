package ru.practicum.events.service;

import ru.practicum.events.dto.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс реализует логику операций для событий
 */
public interface EventsService {

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

    /** Метод получает полную информацию о событии добавленном текущим пользователем
     * @param userId идентификатор пользователя - выполняющего запрос
     * @param eventId идентификатор события
     * @return Полная информация о событии
     */
    EventFullDto getOneEvent(Long userId, Long eventId);

    /** Метод изменения события добавленного текущим пользователем
     * @param userId идентификатор пользователя - изменяющего событие
     * @param eventId идентификатор события
     * @param updateEventUserRequest измененные данные в событии
     * @return полное описание созданного события
     */
    EventFullDto userUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> getAdminEvents(
            List<Long> users,
            List<State> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size
    );

    EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest request);

    List<EventShortDto> getPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size,
            String ip
    );

    EventFullDto getPublicEventById(Long eventId, String ip);
}
