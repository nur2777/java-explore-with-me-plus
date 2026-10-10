package ru.practicum.requests.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.requests.model.Request;

import java.util.List;
import java.util.Optional;


public interface RequestRepository extends JpaRepository<Request,Long> {

    /** Поиск всех запросов у указаного пользователя
     * @param userId - идентификатор пользователя
     * @return список запросов на участие
     */
    List<Request> findAllByRequesterId(Long userId);

    /** Поиск запроса по юзеру и идентификатору события
     * @param userId - идентификатор пользователя
     * @param eventId - идентификатор события
     * @return возвращает объект запроса
     */
    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    /** Считает количество запросов на участие в событии
     * @param eventId - идентификатор события
     * @return Количество событий
     */
    Long countByEventId(Long eventId);
}
