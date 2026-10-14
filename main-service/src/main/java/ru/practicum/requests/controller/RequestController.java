package ru.practicum.requests.controller;


import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.Collection;

/**
 * Интерфейс для контроллера по работе с заявками на участие
 */
public interface RequestController {

    /** Эндпоинт добавления новой заявки
     * @param userId идентификатор пользователя - подающий заявку
     * @param eventId идентификатор события
     * @return объект созданной заявки
     */
    ParticipationRequestDto addNewRequest(Long userId, Long eventId);

    /** Эндпоинт отмены заявки
     * @param userId идентификатор пользователя - отменяющего заявку
     * @param requestId идентификатор заявки
     * @return объект отмененной заявки
     */
    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    /** Эндпоинт получения списка заявок текущего пользователя на участие в чужих событиях
     * @param userId идентификатор пользователя
     * @return список заявок текущего пользователя на участие в чужих событиях
     */
    Collection<ParticipationRequestDto> getRequests(Long userId);

}
