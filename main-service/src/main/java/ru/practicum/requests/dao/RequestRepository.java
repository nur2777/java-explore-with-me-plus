package ru.practicum.requests.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.RequestStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface RequestRepository extends JpaRepository<Request,Long> {

    /** Поиск всех заявок у указанного пользователя
     * @param userId - идентификатор пользователя
     * @return список заявок на участие
     */
    List<Request> findAllByRequesterId(Long userId);

    /** Поиск заявки по юзеру и идентификатору события
     * @param userId - идентификатор пользователя
     * @param eventId - идентификатор события
     * @return возвращает объект заявки
     */
    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    /** Считает количество заявок на участие в событии
     * @param eventId - идентификатор события
     * @return Количество событий
     */
    Long countByEventId(Long eventId);

    /** Поиск всех заявок у события
     * @param eventId - идентификатор события
     * @return список заявок на участие
     */
    List<Request> findAllByEventId(Long eventId);

    /** Поиск всех заявок по списку идентификаторов
     * @param requestIds список идентификаторов
     * @return список заявок на участие
     */
    List<Request> findByIdIn(Collection<Long> requestIds);

    /** Возвращает количество заявок на событие по заданному статусу
     * @param eventId - идентификатор события
     * @param status - статус события
     * @return количество заявок
     */
    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    /** Поиск всех заявок по событию и статусу заявки
     * @param eventId - идентификатор события
     * @param status - статус события
     * @return список заявок на участие
     */
    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);
}
