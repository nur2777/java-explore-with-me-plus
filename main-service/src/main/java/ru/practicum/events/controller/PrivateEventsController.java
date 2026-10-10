package ru.practicum.events.controller;

import ru.practicum.events.dto.*;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс для эндпоинтов закрытой части работы с событиями
 */
public interface PrivateEventsController {

    /** Эндпоинт формирует список событий, добавленных текущим пользователем
     * @param userId идентификатор пользователя - создателя события
     * @param from количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size количество элементов в наборе
     * @return Список событий
     */
    List<EventShortDto> getEvents(Long userId,
                                  Integer from,
                                  Integer size);

    /** Эндпоинт создания нового события
     * @param userId идентификатор пользователя - создателя события
     * @param newEventDto данные о новом событии
     * @return полное описание созданного события
     */
    EventFullDto addNewEvent(Long userId, NewEventDto newEventDto);

    /** Эндпоинт получает полную информацию о событии добавленном текущим пользователем
     * @param userId идентификатор пользователя - выполняющего запрос
     * @param eventId идентификатор события
     * @return Полная информация о событии
     */
    EventFullDto getOneEvent(Long userId, Long eventId);

    /** Эндпоинт изменения события добавленного текущим пользователем
     * @param userId идентификатор пользователя - изменяющего событие
     * @param eventId идентификатор события
     * @param updateEventUserRequest измененные данные в событии
     * @return полное описание созданного события
     */
    EventFullDto userUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    /** Эндпоинт для получения списка запросов на участие в событии текущего пользователя
     * @param userId идентификатор текущего пользователя
     * @param eventId идентификатор события
     * @return список запросов на участие
     */
    Collection<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    /** Эндпоинт изменения статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
     * @param userId идентификатор пользователя - владельца события
     * @param eventId идентификатор события
     * @param eventRequestStatusUpdateRequest список заявок на подтверждение или отказ
     * @return результат обработки запроса
     */
    EventRequestStatusUpdateResult userConfirmRejectRequest(Long userId,
                                                            Long eventId,
                                                            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
