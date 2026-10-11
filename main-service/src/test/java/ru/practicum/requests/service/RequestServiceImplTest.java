
package ru.practicum.requests.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.events.dao.EventRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.exception.ClientErrorException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dao.RequestRepository;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.users.dao.UserRepository;
import ru.practicum.users.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User user;
    private User initiator;
    private Event event;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        initiator = new User();
        initiator.setId(2L);

        event = new Event();
        event.setId(10L);
        event.setInitiator(initiator);
        event.setState(State.PUBLISHED);
        event.setParticipantLimit(5);
        event.setRequestModeration(true);
    }

    private void mockUserAndEvent() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));
    }

    private void mockRequestSave() {
        when(requestRepository.save(any(Request.class)))
                .thenAnswer(invocation -> {
                    Request request = invocation.getArgument(0);
                    request.setId(100L);
                    return request;
                });
    }

    @Test
    void addNewRequestShouldCreatePendingRequest() {
        mockUserAndEvent();
        mockRequestSave();

        ParticipationRequestDto result =
                requestService.addNewRequest(1L, 10L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(10L, result.getEvent());
        assertEquals(1L, result.getRequester());
        assertEquals("PENDING", result.getStatus());

        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void addNewRequestShouldConfirmWithoutModeration() {
        event.setRequestModeration(false);
        mockUserAndEvent();
        mockRequestSave();

        ParticipationRequestDto result =
                requestService.addNewRequest(1L, 10L);

        assertEquals("CONFIRMED", result.getStatus());
    }

    @Test
    void addNewRequestShouldConfirmWhenLimitIsZero() {
        event.setParticipantLimit(0);
        mockUserAndEvent();
        mockRequestSave();

        ParticipationRequestDto result =
                requestService.addNewRequest(1L, 10L);

        assertEquals("CONFIRMED", result.getStatus());
    }

    @Test
    void addNewRequestShouldRejectInitiator() {
        event.setInitiator(user);
        mockUserAndEvent();

        assertThrows(ClientErrorException.class,
                () -> requestService.addNewRequest(1L, 10L));

        verify(requestRepository, never()).save(any());
    }

    @Test
    void addNewRequestShouldRejectPendingEvent() {
        event.setState(State.PENDING);
        mockUserAndEvent();

        assertThrows(ClientErrorException.class,
                () -> requestService.addNewRequest(1L, 10L));

        verify(requestRepository, never()).save(any());
    }

    @Test
    void addNewRequestShouldRejectDuplicate() {
        mockUserAndEvent();

        when(requestRepository.findByRequesterIdAndEventId(1L, 10L))
                .thenReturn(Optional.of(new Request()));

        assertThrows(ClientErrorException.class,
                () -> requestService.addNewRequest(1L, 10L));

        verify(requestRepository, never()).save(any());
    }

    @Test
    void addNewRequestShouldRejectWhenLimitReached() {
        mockUserAndEvent();

        when(requestRepository.countByEventIdAndStatus(
                10L, RequestStatus.CONFIRMED))
                .thenReturn(5L);

        assertThrows(ClientErrorException.class,
                () -> requestService.addNewRequest(1L, 10L));

        verify(requestRepository, never()).save(any());
    }

    @Test
    void addNewRequestShouldAllowWhenLimitNotReached() {
        mockUserAndEvent();
        mockRequestSave();

        when(requestRepository.countByEventIdAndStatus(
                10L, RequestStatus.CONFIRMED))
                .thenReturn(4L);

        ParticipationRequestDto result =
                requestService.addNewRequest(1L, 10L);

        assertNotNull(result);
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void addNewRequestShouldThrowWhenUserNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.addNewRequest(1L, 10L));

        verifyNoInteractions(eventRepository);
    }

    @Test
    void addNewRequestShouldThrowWhenEventNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.addNewRequest(1L, 10L));
    }

    @Test
    void cancelRequestShouldChangeStatusToCanceled() {
        Request request = new Request();
        request.setId(100L);
        request.setEvent(event);
        request.setRequester(user);
        request.setStatus(RequestStatus.PENDING);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(requestRepository.findById(100L))
                .thenReturn(Optional.of(request));

        when(requestRepository.save(request))
                .thenReturn(request);

        ParticipationRequestDto result =
                requestService.cancelRequest(1L, 100L);

        assertEquals("CANCELED", result.getStatus());
        assertEquals(RequestStatus.CANCELED, request.getStatus());

        verify(requestRepository).save(request);
    }

    @Test
    void cancelRequestShouldThrowWhenRequestNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(requestRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.cancelRequest(1L, 100L));
    }

    @Test
    void getRequestsShouldReturnUserRequests() {
        Request request = new Request();
        request.setId(100L);
        request.setEvent(event);
        request.setRequester(user);
        request.setStatus(RequestStatus.CONFIRMED);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(requestRepository.findAllByRequesterId(1L))
                .thenReturn(List.of(request));

        var result = requestService.getRequests(1L);

        assertEquals(1, result.size());
        assertEquals("CONFIRMED",
                result.iterator().next().getStatus());
    }

    @Test
    void getRequestsByEventShouldReturnRequests() {
        Request request = new Request();
        request.setId(100L);
        request.setEvent(event);
        request.setRequester(user);
        request.setStatus(RequestStatus.PENDING);

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        when(requestRepository.findAllByEventId(10L))
                .thenReturn(List.of(request));

        var result = requestService.getRequestsByEvent(10L);

        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
    }

    @Test
    void getRequestsByIdsShouldReturnMatchingRequests() {
        Request request = new Request();
        request.setId(100L);
        request.setEvent(event);
        request.setRequester(user);
        request.setStatus(RequestStatus.CONFIRMED);

        when(requestRepository.findByIdIn(List.of(100L)))
                .thenReturn(List.of(request));

        var result = requestService.getRequestsByIds(List.of(100L));

        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getId());
    }
}
