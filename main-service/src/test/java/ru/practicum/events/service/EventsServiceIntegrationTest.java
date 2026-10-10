package ru.practicum.events.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.client.StatsClient;
import ru.practicum.categories.dao.CategoryRepository;
import ru.practicum.categories.model.Category;
import ru.practicum.events.dao.EventRepository;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.model.Location;
import ru.practicum.exception.ClientErrorException;
import ru.practicum.users.dao.UserRepository;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(EventsServiceImpl.class)
class EventsServiceIntegrationTest {

    @Autowired
    private EventsServiceImpl eventsService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockBean
    private StatsClient statsClient;

    @Test
    void getAdminEvents_shouldFilterByUserStateAndCategory() {
        User firstUser = new User();
        firstUser.setName("First User");
        firstUser.setEmail("first@example.com");
        firstUser = userRepository.save(firstUser);

        User secondUser = new User();
        secondUser.setName("Second User");
        secondUser.setEmail("second@example.com");
        secondUser = userRepository.save(secondUser);

        Category music = new Category();
        music.setName("Music");
        music = categoryRepository.save(music);

        Category sport = new Category();
        sport.setName("Sport");
        sport = categoryRepository.save(sport);

        Event firstEvent = createEvent(
                firstUser, music, State.PUBLISHED, "Concert"
        );

        Event secondEvent = createEvent(
                firstUser, music, State.PENDING, "Festival"
        );

        Event thirdEvent = createEvent(
                secondUser, sport, State.PUBLISHED, "Football"
        );

        firstEvent = eventRepository.save(firstEvent);
        eventRepository.save(secondEvent);
        eventRepository.save(thirdEvent);

        List<EventFullDto> result = eventsService.getAdminEvents(
                List.of(firstUser.getId()),
                List.of(State.PUBLISHED),
                List.of(music.getId()),
                null,
                null,
                0,
                10
        );

        assertEquals(1, result.size());
        assertEquals(firstEvent.getId(), result.get(0).getId());
        assertEquals(State.PUBLISHED, result.get(0).getState());
    }

    private Event createEvent(
            User initiator,
            Category category,
            State state,
            String title
    ) {
        Event event = new Event();

        event.setInitiator(initiator);
        event.setCategory(category);
        event.setState(state);
        event.setTitle(title);
        event.setAnnotation("Test annotation");
        event.setDescription("Test description");
        event.setEventDate(LocalDateTime.now().plusDays(5));
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(true);

        Location location = new Location();
        location.setLat(55.75f);
        location.setLon(37.61f);
        event.setLocation(location);

        return event;
    }

    @Test
    void getAdminEvents_shouldFilterByDateAndApplyPagination() {
        User user = new User();
        user.setName("Pagination User");
        user.setEmail("pagination@example.com");
        user = userRepository.save(user);

        Category category = new Category();
        category.setName("Pagination Category");
        category = categoryRepository.save(category);

        LocalDateTime baseDate = LocalDateTime.now().plusDays(10);

        Event first = createEvent(user, category, State.PUBLISHED, "First");
        first.setEventDate(baseDate.plusDays(1));

        Event second = createEvent(user, category, State.PUBLISHED, "Second");
        second.setEventDate(baseDate.plusDays(2));

        Event third = createEvent(user, category, State.PUBLISHED, "Third");
        third.setEventDate(baseDate.plusDays(3));

        Event fourth = createEvent(user, category, State.PUBLISHED, "Fourth");
        fourth.setEventDate(baseDate.plusDays(10));

        eventRepository.save(first);
        second = eventRepository.save(second);
        eventRepository.save(third);
        eventRepository.save(fourth);

        eventRepository.flush();

        List<EventFullDto> result = eventsService.getAdminEvents(
                null,
                null,
                null,
                baseDate,
                baseDate.plusDays(5),
                1,
                1
        );

        assertEquals(1, result.size());
        assertEquals(second.getId(), result.get(0).getId());
        assertEquals("Second", result.get(0).getTitle());
    }

    @Test
    void getPublicEvents_shouldSearchTextIgnoringCaseAndReturnOnlyPublished() {
        User user = new User();
        user.setName("Public Search User");
        user.setEmail("public-search@example.com");
        user = userRepository.save(user);

        Category category = new Category();
        category.setName("Public Search Category");
        category = categoryRepository.save(category);

        Event first = createEvent(user, category, State.PUBLISHED, "Java Conference");
        first.setAnnotation("Amazing JAVA conference");

        Event second = createEvent(user, category, State.PUBLISHED, "Spring Workshop");
        second.setDescription("Learn java and Spring Boot");

        Event third = createEvent(user, category, State.PENDING, "Private Event");
        third.setAnnotation("JAVA event for developers");

        Event fourth = createEvent(user, category, State.PUBLISHED, "Music Festival");
        fourth.setAnnotation("Live music and dancing");
        fourth.setDescription("A festival for music lovers");

        first = eventRepository.save(first);
        second = eventRepository.save(second);
        eventRepository.save(third);
        eventRepository.save(fourth);

        eventRepository.flush();

        List<EventShortDto> result = eventsService.getPublicEvents(
                "jAvA",
                null,
                null,
                null,
                null,
                false,
                "EVENT_DATE",
                0,
                10,
                "127.0.0.1"
        );

        assertEquals(2, result.size());

        List<Long> eventIds = result.stream()
                .map(EventShortDto::getId)
                .toList();

        assertTrue(eventIds.contains(first.getId()));
        assertTrue(eventIds.contains(second.getId()));
    }

    @Test
    void getPublicEvents_shouldFilterByCategoryPaidAndDateRange() {
        User user = new User();
        user.setName("Filter User");
        user.setEmail("filter@example.com");
        user = userRepository.save(user);

        Category music = new Category();
        music.setName("Filter Music");
        music = categoryRepository.save(music);

        Category sport = new Category();
        sport.setName("Filter Sport");
        sport = categoryRepository.save(sport);

        LocalDateTime baseDate = LocalDateTime.now().plusDays(10);

        Event matching = createEvent(user, music, State.PUBLISHED, "Paid Concert");
        matching.setPaid(true);
        matching.setEventDate(baseDate.plusDays(1));

        Event wrongCategory = createEvent(user, sport, State.PUBLISHED, "Paid Match");
        wrongCategory.setPaid(true);
        wrongCategory.setEventDate(baseDate.plusDays(1));

        Event wrongPaid = createEvent(user, music, State.PUBLISHED, "Free Concert");
        wrongPaid.setPaid(false);
        wrongPaid.setEventDate(baseDate.plusDays(1));

        Event wrongDate = createEvent(user, music, State.PUBLISHED, "Late Concert");
        wrongDate.setPaid(true);
        wrongDate.setEventDate(baseDate.plusDays(10));

        matching = eventRepository.save(matching);
        eventRepository.save(wrongCategory);
        eventRepository.save(wrongPaid);
        eventRepository.save(wrongDate);

        eventRepository.flush();

        List<EventShortDto> result = eventsService.getPublicEvents(
                null,
                List.of(music.getId()),
                true,
                baseDate,
                baseDate.plusDays(5),
                false,
                "EVENT_DATE",
                0,
                10,
                "127.0.0.1"
        );

        assertEquals(1, result.size());
        assertEquals(matching.getId(), result.get(0).getId());
        assertEquals("Paid Concert", result.get(0).getTitle());
    }

    @Test
    void updateAdminEvent_shouldPublishPendingEvent() {
        User user = new User();
        user.setName("Publish User");
        user.setEmail("publish@example.com");
        user = userRepository.save(user);

        Category category = new Category();
        category.setName("Publish Category");
        category = categoryRepository.save(category);

        Event event = createEvent(
                user,
                category,
                State.PENDING,
                "Event to publish"
        );

        event.setEventDate(LocalDateTime.now().plusDays(5));
        event = eventRepository.saveAndFlush(event);

        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAction.PUBLISH_EVENT);

        EventFullDto result = eventsService.updateAdminEvent(
                event.getId(),
                request
        );

        assertEquals(State.PUBLISHED, result.getState());
        assertNotNull(result.getPublishedOn());

        Event savedEvent = eventRepository.findById(event.getId())
                .orElseThrow();

        assertEquals(State.PUBLISHED, savedEvent.getState());
        assertNotNull(savedEvent.getPublishedOn());
    }

    @Test
    void updateAdminEvent_shouldRejectPublishingAlreadyPublishedEvent() {
        User user = new User();
        user.setName("Already Published User");
        user.setEmail("already-published@example.com");
        user = userRepository.save(user);

        Category category = new Category();
        category.setName("Already Published Category");
        category = categoryRepository.save(category);

        Event event = createEvent(
                user,
                category,
                State.PUBLISHED,
                "Already published event"
        );

        event.setEventDate(LocalDateTime.now().plusDays(5));
        event = eventRepository.saveAndFlush(event);

        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAction.PUBLISH_EVENT);

        Long eventId = event.getId();

        assertThrows(
                ClientErrorException.class,
                () -> eventsService.updateAdminEvent(eventId, request)
        );

        Event savedEvent = eventRepository.findById(eventId)
                .orElseThrow();

        assertEquals(State.PUBLISHED, savedEvent.getState());
    }

    @Test
    void updateAdminEvent_shouldCancelPendingEvent() {
        User user = new User();
        user.setName("Cancel User");
        user.setEmail("cancel@example.com");
        user = userRepository.save(user);

        Category category = new Category();
        category.setName("Cancel Category");
        category = categoryRepository.save(category);

        Event event = createEvent(
                user, category, State.PENDING, "Event to cancel"
        );

        event = eventRepository.saveAndFlush(event);

        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAction.REJECT_EVENT);

        EventFullDto result = eventsService.updateAdminEvent(
                event.getId(), request
        );

        assertEquals(State.CANCELED, result.getState());

        Event savedEvent = eventRepository.findById(event.getId())
                .orElseThrow();

        assertEquals(State.CANCELED, savedEvent.getState());
    }

    @Test
    void updateAdminEvent_shouldRejectPublishingCanceledEvent() {
        User user = new User();
        user.setName("Canceled User");
        user.setEmail("canceled@example.com");
        user = userRepository.save(user);

        Category category = new Category();
        category.setName("Canceled Category");
        category = categoryRepository.save(category);

        Event event = createEvent(
                user, category, State.CANCELED, "Canceled event"
        );

        event = eventRepository.saveAndFlush(event);

        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAction.PUBLISH_EVENT);

        Long eventId = event.getId();

        assertThrows(
                ClientErrorException.class,
                () -> eventsService.updateAdminEvent(eventId, request)
        );

        Event savedEvent = eventRepository.findById(eventId)
                .orElseThrow();

        assertEquals(State.CANCELED, savedEvent.getState());
    }
}