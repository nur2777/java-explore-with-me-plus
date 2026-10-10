package ru.practicum.events.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.events.model.Event;

import java.util.List;
import java.util.Set;

public interface EventRepository extends
        JpaRepository<Event, Long>,
        JpaSpecificationExecutor<Event> {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    List<Event> findByCategoryId(Long categoryId);

    /** Поиск всех событий по списку уникальных идентификаторов
     * @param eventIds список идентификаторов событий
     * @return список уникальных событий
     */
    Set<Event> findByIdIn(Set<Long> eventIds);
}