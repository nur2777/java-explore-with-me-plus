package ru.practicum.events.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.categories.dao.CategoryRepository;
import ru.practicum.categories.model.Category;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.events.dao.EventRepository;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.exception.ClientErrorException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.users.dao.UserRepository;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventsServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private StatsClient statsClient;

    @InjectMocks
    private EventsServiceImpl eventsService;

    private Event event;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);
        event.setState(State.PENDING);
        event.setEventDate(LocalDateTime.now().plusDays(2));

        Category category = new Category();
        category.setId(1L);
        event.setCategory(category);

        User initiator = new User();
        initiator.setId(1L);
        event.setInitiator(initiator);
    }

    @Test
    void updateAdminEvent_shouldPublishPendingEvent() {
        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAction.PUBLISH_EVENT);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(eventRepository.save(any(Event.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EventFullDto result = eventsService.updateAdminEvent(1L, request);

        assertEquals(State.PUBLISHED, event.getState());
        assertNotNull(event.getPublishedOn());
        assertNotNull(result);

        verify(eventRepository).save(event);
    }

    @Test
    void updateAdminEvent_shouldRejectPublicationWhenEventDateIsTooSoon() {
        // Подготавливаем событие, которое начнётся через 20 минут
        event.setEventDate(LocalDateTime.now().plusMinutes(20));

        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAction.PUBLISH_EVENT);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        // Проверяем, что публикация вызывает исключение
        assertThrows(
                ClientErrorException.class,
                () -> eventsService.updateAdminEvent(1L, request)
        );

        // Убеждаемся, что событие не было сохранено
        verify(eventRepository, never()).save(any(Event.class));

        // И его состояние не изменилось
        assertEquals(State.PENDING, event.getState());
    }

    @Test
    void updateAdminEvent_shouldRejectAlreadyPublishedEvent() {
        event.setState(State.PUBLISHED);

        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAction.PUBLISH_EVENT);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        assertThrows(
                ClientErrorException.class,
                () -> eventsService.updateAdminEvent(1L, request)
        );

        verify(eventRepository, never()).save(any(Event.class));

        assertEquals(State.PUBLISHED, event.getState());
    }

    @Test
    void updateAdminEvent_shouldRejectPendingEvent() {
        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAction.REJECT_EVENT);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(eventRepository.save(any(Event.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EventFullDto result = eventsService.updateAdminEvent(1L, request);

        assertNotNull(result);
        assertEquals(State.CANCELED, event.getState());

        verify(eventRepository).save(event);
    }

    @Test
    void updateAdminEvent_shouldNotRejectPublishedEvent() {
        event.setState(State.PUBLISHED);

        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAction.REJECT_EVENT);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        assertThrows(
                ClientErrorException.class,
                () -> eventsService.updateAdminEvent(1L, request)
        );

        verify(eventRepository, never()).save(any(Event.class));

        assertEquals(State.PUBLISHED, event.getState());
    }

    @Test
    void updateAdminEvent_shouldThrowExceptionWhenEventNotFound() {
        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setStateAction(StateAction.PUBLISH_EVENT);

        when(eventRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> eventsService.updateAdminEvent(999L, request)
        );

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void getPublicEventById_shouldThrowExceptionWhenEventNotPublished() {

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        assertThrows(
                NotFoundException.class,
                () -> eventsService.getPublicEventById(
                        1L,
                        "127.0.0.1"
                )
        );

        // Неопубликованное событие не должно учитываться в статистике
        verifyNoInteractions(statsClient);
    }

    @Test
    void getPublicEventById_shouldReturnPublishedEvent() {
        event.setState(State.PUBLISHED);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(statsClient.getStats(any(StatsRequestDto.class)))
                .thenReturn(Collections.emptyList());

        EventFullDto result = eventsService.getPublicEventById(
                1L,
                "127.0.0.1"
        );

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(0, result.getViews());

        verify(statsClient).hit(any(HitDto.class));
        verify(statsClient).getStats(any(StatsRequestDto.class));
    }

    @Test
    void getPublicEventById_shouldReturnCorrectViews() {
        event.setState(State.PUBLISHED);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        StatDto stat = new StatDto(
                "ewm-main-service",
                "/events/1",
                15L
        );

        when(statsClient.getStats(any(StatsRequestDto.class)))
                .thenReturn(List.of(stat));

        EventFullDto result = eventsService.getPublicEventById(
                1L,
                "127.0.0.1"
        );

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(15, result.getViews());

        verify(statsClient).hit(any(HitDto.class));
        verify(statsClient).getStats(any(StatsRequestDto.class));
    }

    @Test
    void getPublicEvents_shouldReturnEmptyListWhenNoEventsFound() {
        when(eventRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        List<EventShortDto> result = eventsService.getPublicEvents(
                null,           // text
                null,           // categories
                null,           // paid
                null,           // rangeStart
                null,           // rangeEnd
                false,          // onlyAvailable
                "EVENT_DATE",   // sort
                0,              // from
                10,             // size
                "127.0.0.1"     // ip
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(eventRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );

        verify(statsClient).hit(any(HitDto.class));
        verify(statsClient, never())
                .getStats(any(StatsRequestDto.class));
    }

    @Test
    void getPublicEvents_shouldUseCorrectPagination() {
        int from = 15;
        int size = 10;

        when(eventRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of()));

        List<EventShortDto> result = eventsService.getPublicEvents(
                null,           // text
                null,           // categories
                null,           // paid
                null,           // rangeStart
                null,           // rangeEnd
                false,          // onlyAvailable
                "EVENT_DATE",   // sort
                from,           // from
                size,           // size
                "127.0.0.1"     // ip
        );

        assertTrue(result.isEmpty());

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(eventRepository).findAll(
                any(Specification.class),
                pageableCaptor.capture()
        );

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(15, pageable.getOffset());
        assertEquals(10, pageable.getPageSize());

        verify(statsClient).hit(any(HitDto.class));
        verify(statsClient, never())
                .getStats(any(StatsRequestDto.class));
    }

    @Test
    void getPublicEvents_shouldSortEventsByViews() {
        // Подготавливаем три опубликованных события
        event.setState(State.PUBLISHED);

        Event secondEvent = new Event();
        secondEvent.setId(2L);
        secondEvent.setState(State.PUBLISHED);
        secondEvent.setEventDate(LocalDateTime.now().plusDays(3));
        secondEvent.setCategory(event.getCategory());
        secondEvent.setInitiator(event.getInitiator());

        Event thirdEvent = new Event();
        thirdEvent.setId(3L);
        thirdEvent.setState(State.PUBLISHED);
        thirdEvent.setEventDate(LocalDateTime.now().plusDays(4));
        thirdEvent.setCategory(event.getCategory());
        thirdEvent.setInitiator(event.getInitiator());

        when(eventRepository.findAll(
                any(Specification.class),
                any(Sort.class)
        )).thenReturn(List.of(event, secondEvent, thirdEvent));

        // Имитируем ответ сервера статистики
        List<StatDto> stats = List.of(
                new StatDto("ewm-main-service", "/events/1", 5L),
                new StatDto("ewm-main-service", "/events/2", 30L),
                new StatDto("ewm-main-service", "/events/3", 15L)
        );

        when(statsClient.getStats(any(StatsRequestDto.class)))
                .thenReturn(stats);

        List<EventShortDto> result = eventsService.getPublicEvents(
                null,
                null,
                null,
                null,
                null,
                false,
                "VIEWS",
                0,
                10,
                "127.0.0.1"
        );

        assertEquals(3, result.size());

        assertEquals(2L, result.get(0).getId());
        assertEquals(30, result.get(0).getViews());

        assertEquals(3L, result.get(1).getId());
        assertEquals(15, result.get(1).getViews());

        assertEquals(1L, result.get(2).getId());
        assertEquals(5, result.get(2).getViews());

        verify(statsClient).hit(any(HitDto.class));
        verify(statsClient).getStats(any(StatsRequestDto.class));
    }

    @Test
    void getPublicEvents_shouldApplyPaginationAfterSortingByViews() {
        event.setState(State.PUBLISHED);

        Event secondEvent = new Event();
        secondEvent.setId(2L);
        secondEvent.setState(State.PUBLISHED);
        secondEvent.setEventDate(LocalDateTime.now().plusDays(3));
        secondEvent.setCategory(event.getCategory());
        secondEvent.setInitiator(event.getInitiator());

        Event thirdEvent = new Event();
        thirdEvent.setId(3L);
        thirdEvent.setState(State.PUBLISHED);
        thirdEvent.setEventDate(LocalDateTime.now().plusDays(4));
        thirdEvent.setCategory(event.getCategory());
        thirdEvent.setInitiator(event.getInitiator());

        when(eventRepository.findAll(
                any(Specification.class),
                any(Sort.class)
        )).thenReturn(List.of(event, secondEvent, thirdEvent));

        when(statsClient.getStats(any(StatsRequestDto.class)))
                .thenReturn(List.of(
                        new StatDto("ewm-main-service", "/events/1", 5L),
                        new StatDto("ewm-main-service", "/events/2", 30L),
                        new StatDto("ewm-main-service", "/events/3", 15L)
                ));

        List<EventShortDto> result = eventsService.getPublicEvents(
                null,           // text
                null,           // categories
                null,           // paid
                null,           // rangeStart
                null,           // rangeEnd
                false,          // onlyAvailable
                "VIEWS",        // sort
                1,              // from
                1,              // size
                "127.0.0.1"     // ip
        );

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(15, result.get(0).getViews());

        verify(eventRepository).findAll(
                any(Specification.class),
                any(Sort.class)
        );

        verify(statsClient).hit(any(HitDto.class));
        verify(statsClient).getStats(any(StatsRequestDto.class));
    }

    @Test
    void getAdminEvents_shouldUseCorrectPagination() {
        int from = 15;
        int size = 10;

        when(eventRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        List<EventFullDto> result = eventsService.getAdminEvents(
                null,   // users
                null,   // states
                null,   // categories
                null,   // rangeStart
                null,   // rangeEnd
                from,
                size
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(eventRepository).findAll(
                any(Specification.class),
                pageableCaptor.capture()
        );

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(15, pageable.getOffset());
        assertEquals(10, pageable.getPageSize());

        assertEquals(
                Sort.Direction.ASC,
                pageable.getSort().getOrderFor("id").getDirection()
        );

        verifyNoInteractions(statsClient);
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 10",
            "0, 0",
            "0, -5",
            "-10, -1"
    })
    void getAdminEvents_shouldRejectInvalidPagination(
            int from,
            int size
    ) {
        assertThrows(
                ValidationException.class,
                () -> eventsService.getAdminEvents(
                        null,   // users
                        null,   // states
                        null,   // categories
                        null,   // rangeStart
                        null,   // rangeEnd
                        from,
                        size
                )
        );

        verifyNoInteractions(eventRepository);
        verifyNoInteractions(statsClient);
    }

    @Test
    void getAdminEvents_shouldRejectInvalidDateRange() {
        LocalDateTime rangeStart = LocalDateTime.now().plusDays(10);
        LocalDateTime rangeEnd = LocalDateTime.now().plusDays(5);

        assertThrows(
                ValidationException.class,
                () -> eventsService.getAdminEvents(
                        null,        // users
                        null,        // states
                        null,        // categories
                        rangeStart,  // rangeStart
                        rangeEnd,    // rangeEnd
                        0,           // from
                        10           // size
                )
        );

        verifyNoInteractions(eventRepository);
        verifyNoInteractions(statsClient);
    }
}