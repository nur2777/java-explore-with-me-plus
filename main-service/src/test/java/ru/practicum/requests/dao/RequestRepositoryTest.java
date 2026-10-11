
package ru.practicum.requests.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.events.model.Event;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.users.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.ANY
)
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Event event;
    private User requester;

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager()
                .createNativeQuery("""
                        INSERT INTO users (user_id, name, email)
                        VALUES (101, 'Initiator', 'initiator@test.ru')
                        """)
                .executeUpdate();

        entityManager.getEntityManager()
                .createNativeQuery("""
                        INSERT INTO users (user_id, name, email)
                        VALUES (102, 'Requester', 'requester@test.ru')
                        """)
                .executeUpdate();

        entityManager.getEntityManager()
                .createNativeQuery("""
                        INSERT INTO categories (id, name)
                        VALUES (101, 'Concerts')
                        """)
                .executeUpdate();

        entityManager.getEntityManager()
                .createNativeQuery("""
                        INSERT INTO events (
                            id, initiator_id, annotation,
                            category_id, description, event_date,
                            location_lat, location_lon, paid,
                            participant_limit, request_moderation,
                            title, state, creation_date
                        )
                        VALUES (
                            101, 101, 'Test annotation',
                            101, 'Test description',
                            DATEADD('DAY', 7, CURRENT_TIMESTAMP),
                            55.75, 37.61, FALSE,
                            10, TRUE,
                            'Test event', 'PUBLISHED',
                            CURRENT_TIMESTAMP
                        )
                        """)
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();

        event = entityManager.getEntityManager()
                .getReference(Event.class, 101L);

        requester = entityManager.getEntityManager()
                .getReference(User.class, 102L);
    }

    private Request saveRequest(RequestStatus status) {
        Request request = new Request();
        request.setEvent(event);
        request.setRequester(requester);
        request.setStatus(status);

        return requestRepository.saveAndFlush(request);
    }

    @Test
    void shouldSaveAndFindRequest() {
        Request saved = saveRequest(RequestStatus.PENDING);

        entityManager.clear();

        Request found = requestRepository.findById(saved.getId())
                .orElseThrow();

        assertEquals(RequestStatus.PENDING, found.getStatus());
        assertEquals(101L, found.getEvent().getId());
        assertEquals(102L, found.getRequester().getId());
        assertNotNull(found.getCreated());
    }

    @Test
    void shouldFindRequestsByRequesterId() {
        saveRequest(RequestStatus.PENDING);

        List<Request> result =
                requestRepository.findAllByRequesterId(102L);

        assertEquals(1, result.size());
        assertEquals(102L, result.get(0).getRequester().getId());
    }

    @Test
    void shouldFindRequestByRequesterAndEvent() {
        saveRequest(RequestStatus.CONFIRMED);

        var result = requestRepository
                .findByRequesterIdAndEventId(102L, 101L);

        assertTrue(result.isPresent());
        assertEquals(RequestStatus.CONFIRMED,
                result.orElseThrow().getStatus());
    }

    @Test
    void shouldReturnEmptyForUnknownRequester() {
        saveRequest(RequestStatus.PENDING);

        var result = requestRepository
                .findByRequesterIdAndEventId(999L, 101L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCountOnlyConfirmedRequests() {
        saveRequest(RequestStatus.CONFIRMED);
        saveRequest(RequestStatus.PENDING);
        saveRequest(RequestStatus.CANCELED);

        Long count = requestRepository.countByEventIdAndStatus(
                101L, RequestStatus.CONFIRMED);

        assertEquals(1L, count);
    }

    @Test
    void shouldFindAllRequestsByEventId() {
        saveRequest(RequestStatus.PENDING);
        saveRequest(RequestStatus.CONFIRMED);

        List<Request> result =
                requestRepository.findAllByEventId(101L);

        assertEquals(2, result.size());
    }

    @Test
    void shouldFindRequestsByIds() {
        Request first = saveRequest(RequestStatus.PENDING);
        Request second = saveRequest(RequestStatus.CONFIRMED);
        saveRequest(RequestStatus.CANCELED);

        List<Request> result = requestRepository.findByIdIn(
                List.of(first.getId(), second.getId()));

        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(request ->
                        request.getStatus() != RequestStatus.CANCELED));
    }

    @Test
    void shouldFindRequestsByEventAndStatus() {
        saveRequest(RequestStatus.CONFIRMED);
        saveRequest(RequestStatus.PENDING);

        List<Request> result =
                requestRepository.findAllByEventIdAndStatus(
                        101L, RequestStatus.CONFIRMED);

        assertEquals(1, result.size());
        assertEquals(RequestStatus.CONFIRMED,
                result.get(0).getStatus());
    }

    @Test
    void shouldCountAllRequestsForEvent() {
        saveRequest(RequestStatus.PENDING);
        saveRequest(RequestStatus.CONFIRMED);

        Long count = requestRepository.countByEventId(101L);

        assertEquals(2L, count);
    }
}
