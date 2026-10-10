package ru.practicum.events.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.events.model.Event;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void findAll_shouldFilterByStateAndCategory() {
        // Создаём пользователя
        jdbcTemplate.update(
                "INSERT INTO users (user_id, name, email) VALUES (?, ?, ?)",
                101L, "Test User", "test101@example.com"
        );

        // Создаём две категории
        jdbcTemplate.update(
                "INSERT INTO categories (id, name) VALUES (?, ?)",
                101L, "Music"
        );

        jdbcTemplate.update(
                "INSERT INTO categories (id, name) VALUES (?, ?)",
                102L, "Sport"
        );

        // Три события с разными состояниями и категориями
        insertEvent(101L, "PUBLISHED", 101L);
        insertEvent(102L, "PENDING", 101L);
        insertEvent(103L, "CANCELED", 102L);

        // Фильтр по состоянию и категории
        Specification<Event> specification = (root, query, cb) ->
                cb.and(
                        cb.equal(root.get("state"), "PUBLISHED"),
                        cb.equal(root.get("category").get("id"), 101L)
                );

        Page<Event> result = eventRepository.findAll(
                specification,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(101L, result.getContent().get(0).getId());
        assertEquals("PUBLISHED", result.getContent().get(0).getState());
        assertEquals(101L,
                result.getContent().get(0).getCategory().getId());
    }

    private void insertEvent(Long id, String state, Long categoryId) {
        jdbcTemplate.update("""
                INSERT INTO events (
                    id,
                    initiator_id,
                    annotation,
                    category_id,
                    description,
                    event_date,
                    location_lat,
                    location_lon,
                    paid,
                    participant_limit,
                    request_moderation,
                    title,
                    state,
                    creation_date
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                101L,
                "Test annotation",
                categoryId,
                "Test description",
                LocalDateTime.now().plusDays(5),
                55.75,
                37.61,
                false,
                0,
                true,
                "Test event " + id,
                state,
                LocalDateTime.now()
        );
    }
}