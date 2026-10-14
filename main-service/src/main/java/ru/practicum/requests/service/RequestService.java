package ru.practicum.requests.service;

import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс реализует логику CRUD-операций для сущности Заявка на участие
 */
public interface RequestService {

    /** Метод добавления новой заявки
     * @param userId идентификатор пользователя - подающий заявку
     * @param eventId идентификатор события
     * @return объект созданной заявки
     */
    ParticipationRequestDto addNewRequest(Long userId, Long eventId);

    /** Метод отмены заявки
     * @param userId идентификатор пользователя - отменяющего заявку
     * @param requestId идентификатор заявки
     * @return объект отмененной заявки
     */
    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    /** Метод получения списка заявок текущего пользователя на участие в чужих событиях
     * @param userId идентификатор пользователя
     * @return список заявок текущего пользователя на участие в чужих событиях
     */
    Collection<ParticipationRequestDto> getRequests(Long userId);

    /** Метод получения списка заявок для указанного события
     * @param eventId идентификатор события
     * @return список заявок для события
     */
    List<ParticipationRequestDto> getRequestsByEvent(Long eventId);

    /** Метод получения списка заявок для по списку идентификаторов
     * @param requestIds список идентификаторов
     * @return список заявок для события
     */
    List<ParticipationRequestDto> getRequestsByIds(List<Long> requestIds);
}
