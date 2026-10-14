package ru.practicum.events.service;

import ru.practicum.events.dto.*;
import ru.practicum.events.model.State;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;
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

    /** Метод для получения списка запросов на участие в событии текущего пользователя
     * @param userId идентификатор текущего пользователя
     * @param eventId идентификатор события
     * @return список запросов на участие
     */
    Collection<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    /** Метод изменения статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
     * @param userId идентификатор пользователя - владельца события
     * @param eventId идентификатор события
     * @param eventRequestStatusUpdateRequest список заявок на подтверждение или отказ
     * @return результат обработки запроса
     */
    EventRequestStatusUpdateResult userConfirmRejectRequest(Long userId,
                                                            Long eventId,
                                                            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

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
