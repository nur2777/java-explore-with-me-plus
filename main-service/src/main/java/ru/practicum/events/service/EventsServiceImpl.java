package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import ru.practicum.categories.dao.CategoryRepository;
import ru.practicum.client.StatsClient;
import ru.practicum.categories.model.Category;
import ru.practicum.common.EwmUtils;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.events.dao.EventRepository;
import ru.practicum.events.dto.*;
import ru.practicum.events.mapping.EventsMap;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.exception.ClientErrorException;
import ru.practicum.events.pagination.OffsetBasedPageRequest;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.dao.RequestRepository;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapping.RequestMap;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.users.dao.UserRepository;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventsServiceImpl implements EventsService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private final StatsClient statsClient;
    private final CategoryRepository categoryRepository;

    @Override
    public List<EventShortDto> getEvents(Long userId,
                                         Integer from,
                                         Integer size) {
        userCheck(userId);
        Pageable page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId,page);
        return events.stream()
                .map(EventsMap::eventShortDtoFromEvent)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto addNewEvent(Long userId, NewEventDto newEventDto) {
        User initiator = userCheck(userId);
        if (newEventDto == null) {
            throw new ValidationException("Данные нового события должны быть заполнены!");
        }
        if (newEventDto.getEventDate() != null && newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время на которые намечено событие (" + newEventDto.getEventDate().format(dateTimeFormatter) + ") не может быть раньше, " +
                    "чем через два часа от текущего момента (" + LocalDateTime.now().plusHours(2).format(dateTimeFormatter) + ")");
        }
        checkNegativeLimit(newEventDto.getParticipantLimit());
        Event newEvent = EventsMap.newEventDtoToEvent(newEventDto);
        newEvent.setInitiator(initiator);
        newEvent.setState(State.PENDING);
        Event createdEvent = eventRepository.save(newEvent);
        //TODO необходимо заполнять confirmedRequests и views
        return EventsMap.eventFullDtoFromEvent(createdEvent);
    }

    @Override
    public EventFullDto getOneEvent(Long userId, Long eventId) {
        userCheck(userId);
        Event event = eventCheck(eventId);
        //TODO необходимо заполнять confirmedRequests и views
        return EventsMap.eventFullDtoFromEvent(event);
    }

    @Override
    @Transactional
    public EventFullDto userUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        userCheck(userId);
        Event event = eventCheck(eventId);
        if (updateEventUserRequest == null) {
            throw new ValidationException("Данные об изменении в событии должны быть заполнены!");
        }
        if (!Objects.equals(userId,event.getInitiator().getId())) {
            throw new ValidationException("Пользователь выполняющий изменение не является инициатором события. Редактировать событие может только его инициатор.");
        }
        if (!event.getState().equals(State.PENDING) && !event.getState().equals(State.CANCELED)) {
            throw new ClientErrorException("Изменить можно только отмененные события или события в состоянии ожидания модерации!");
        }
        if (updateEventUserRequest.getEventDate() != null && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время на которые намечено событие (" + updateEventUserRequest.getEventDate().format(dateTimeFormatter)
                    + ") не может быть раньше, чем через два часа от текущего момента (" + LocalDateTime.now().plusHours(2).format(dateTimeFormatter) + ")");
        }
        checkNegativeLimit(updateEventUserRequest.getParticipantLimit());
        EventsMap.updateEventUserRequestToEvent(updateEventUserRequest,event);
        Event updatedEvent = eventRepository.save(event);
        //TODO необходимо заполнять confirmedRequests и views
        return EventsMap.eventFullDtoFromEvent(updatedEvent);
    }

    @Override
    public Collection<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        userCheck(userId);
        Event event = eventCheck(eventId);
        if (!Objects.equals(userId,event.getInitiator().getId())) {
            throw new ValidationException("Пользователь выполняющий запрос не является инициатором события. Запрос может делать только его инициатор.");
        }
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(RequestMap::requestToParticipationRequestDto).toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult userConfirmRejectRequest(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        userCheck(userId);
        Event event = eventCheck(eventId);
        if (!Objects.equals(userId,event.getInitiator().getId())) {
            throw new ValidationException("Пользователь выполняющий изменение не является инициатором события. Изменение может делать только его инициатор.");
        }
        if (eventRequestStatusUpdateRequest == null) {
            throw new ValidationException("Список заявок на подтверждение/отклонение пуст");
        }
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();
        //List<Request> confirmedReqs = requestRepository.findAllByEventIdAndStatus(eventId,RequestStatus.CONFIRMED);
        Long confirmedReqsCount = requestRepository.countByEventIdAndStatus(eventId,RequestStatus.CONFIRMED);
        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
            //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
            if (event.getParticipantLimit() != null && event.getParticipantLimit() == 0
                    || !event.getRequestModeration()) {
                List<Request> requests = requestRepository.findByIdIn(requestIds);
                result.setConfirmedRequests(requests.stream().map(RequestMap::requestToParticipationRequestDto).toList());
                return result;
            }
            //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие
            if (event.getParticipantLimit() != null  && confirmedReqsCount >= event.getParticipantLimit()) {
                throw new ClientErrorException("Достигнут лимит по заявкам на данное событие.");
            }
        }
        List<Request> requests = requestRepository.findByIdIn(requestIds);
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        if (requests != null && !requests.isEmpty()) {
            for (Request request : requests) {
                //статус можно изменить только у заявок, находящихся в состоянии ожидания
                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    throw new ClientErrorException("Статус можно изменить только у заявки, находящейся в состоянии ожидания.");
                }
                // если текущий пользователь при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
                if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED) ||
                        requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(request);
                } else {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(request);
                }
                requestRepository.saveAndFlush(request);
            }
            result.setConfirmedRequests(confirmedRequests.stream().map(RequestMap::requestToParticipationRequestDto).toList());
            result.setRejectedRequests(rejectedRequests.stream().map(RequestMap::requestToParticipationRequestDto).toList());
            return result;
        } else {
            throw new ClientErrorException("Не найдены существующие заявки по заданному списку");
        }
    }

    @Override
    public List<EventFullDto> getAdminEvents(
            List<Long> users,
            List<State> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size
    ) {
        if (from == null || from < 0 || size == null || size <= 0) {
            throw new ValidationException("Некорректные параметры пагинации");
        }

        if (rangeStart != null && rangeEnd != null
                && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Дата начала позже даты окончания");
        }

        Specification<Event> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (users != null && !users.isEmpty()) {
                predicates.add(root.get("initiator").get("id").in(users));
            }

            if (states != null && !states.isEmpty()) {
                List<String> stateNames = states.stream()
                        .map(State::name)
                        .toList();

                predicates.add(root.get("state").in(stateNames));
            }

            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categories));
            }

            if (rangeStart != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("eventDate"), rangeStart
                ));
            }

            if (rangeEnd != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("eventDate"), rangeEnd
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = new OffsetBasedPageRequest(
                from,
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );

        return eventRepository.findAll(specification, pageable)
                .getContent()
                .stream()
                .map(EventsMap::eventFullDtoFromEvent)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateAdminEvent(
            Long eventId,
            UpdateEventAdminRequest request) {

        Event event = eventCheck(eventId);

        if (request == null) {
            throw new ValidationException("Данные обновления отсутствуют");
        }

        // Проверяем новую дату, не должна быть уже наступившей
        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Новая дата события не должна быть уже наступившей, т.е. она должна быть в будущем");
        }

        // Проверяем новую дату, если администратор её передал
        if (request.getEventDate() != null
                && request.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ClientErrorException(
                    "Дата события должна быть не раньше чем через час"
            );
        }

        // Проверяем возможность изменения состояния
        if (request.getStateAction() == StateAction.PUBLISH_EVENT
                && !State.PENDING.equals(event.getState())) {
            throw new ClientErrorException(
                    "Публиковать можно только события в состоянии PENDING"
            );
        }

        if (request.getStateAction() == StateAction.REJECT_EVENT
                && State.PUBLISHED.equals(event.getState())) {
            throw new ClientErrorException(
                    "Нельзя отклонить опубликованное событие"
            );
        }

        // Обновляем переданные поля
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }

        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }

        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }

        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }

        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }

        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }

        if (request.getParticipantLimit() != null) {
            checkNegativeLimit(request.getParticipantLimit());
            event.setParticipantLimit(request.getParticipantLimit());
        }

        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }

        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            "Категория с id " + request.getCategory() + " не найдена"
                    ));

            event.setCategory(category);
        }

        // Публикуем или отклоняем событие
        if (request.getStateAction() == StateAction.PUBLISH_EVENT) {

            // Проверяем итоговую дату, даже если администратор её не менял
            if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ClientErrorException(
                        "До начала события должно оставаться не менее часа"
                );
            }

            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        }

        if (request.getStateAction() == StateAction.REJECT_EVENT) {
            event.setState(State.CANCELED);
        }

        return EventsMap.eventFullDtoFromEvent(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size,
            String ip
    ) {
        if (from == null || from < 0 || size == null || size <= 0) {
            throw new ValidationException("Некорректные параметры пагинации");
        }

        if (rangeStart != null && rangeEnd != null
                && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException(
                    "Дата начала не может быть позже даты окончания"
            );
        }

        if (sort != null
                && !sort.equals("EVENT_DATE")
                && !sort.equals("VIEWS")) {
            throw new ValidationException("Неизвестный тип сортировки");
        }

        recordHit("/events", ip);

        Specification<Event> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(root.get("state"), State.PUBLISHED.name())
            );

            if (text != null && !text.isBlank()) {
                String searchText = "%"
                        + text.toLowerCase(Locale.ROOT)
                        + "%";

                predicates.add(cb.or(
                        cb.like(
                                cb.lower(root.get("annotation")),
                                searchText
                        ),
                        cb.like(
                                cb.lower(root.get("description")),
                                searchText
                        )
                ));
            }

            if (categories != null && !categories.isEmpty()) {
                predicates.add(
                        root.get("category").get("id").in(categories)
                );
            }

            if (paid != null) {
                predicates.add(cb.equal(root.get("paid"), paid));
            }

            if (rangeStart == null && rangeEnd == null) {
                predicates.add(
                        cb.greaterThan(
                                root.get("eventDate"),
                                LocalDateTime.now()
                        )
                );
            } else {
                if (rangeStart != null) {
                    predicates.add(cb.greaterThanOrEqualTo(
                            root.get("eventDate"), rangeStart
                    ));
                }

                if (rangeEnd != null) {
                    predicates.add(cb.lessThanOrEqualTo(
                            root.get("eventDate"), rangeEnd
                    ));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sorting = Sort.by(
                Sort.Direction.ASC, "eventDate"
        ).and(Sort.by(Sort.Direction.ASC, "id"));

        List<Event> events;

        if ("VIEWS".equals(sort)) {
            // Для сортировки по просмотрам нужны все подходящие события
            events = eventRepository.findAll(specification, sorting);
        } else {
            // Для сортировки по дате получаем только нужную страницу
            Pageable pageable = new OffsetBasedPageRequest(
                    from,
                    size,
                    sorting
            );

            events = eventRepository.findAll(
                    specification, pageable
            ).getContent();
        }

        Map<String, Long> views = getEventViews(events);

        List<EventShortDto> result = events.stream()
                .map(event -> {
                    EventShortDto dto =
                            EventsMap.eventShortDtoFromEvent(event);

                    String uri = "/events/" + event.getId();
                    long count = views.getOrDefault(uri, 0L);

                    dto.setViews(Math.toIntExact(count));

                    return dto;
                })
                .toList();

        if ("VIEWS".equals(sort)) {
            return result.stream()
                    .sorted(
                            Comparator.comparing(
                                    EventShortDto::getViews
                            ).reversed()
                    )
                    .skip(from)
                    .limit(size)
                    .toList();
        }

        return result;
    }

    @Override
    public EventFullDto getPublicEventById(Long eventId, String ip) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new NotFoundException("Событие не найдено: " + eventId)
                );

        if (!State.PUBLISHED.name().equals(event.getState())) {
            throw new NotFoundException("Событие не найдено: " + eventId);
        }

        String uri = "/events/" + eventId;
        recordHit(uri, ip);

        StatsRequestDto statsRequest = StatsRequestDto.builder()
                .start(LocalDateTime.of(2000, 1, 1, 0, 0))
                .end(LocalDateTime.now().plusSeconds(1))
                .uris(List.of(uri))
                .unique(true)
                .build();

        List<StatDto> stats = statsClient.getStats(statsRequest);

        long views = stats.stream()
                .filter(stat -> uri.equals(stat.getUri()))
                .mapToLong(StatDto::getHits)
                .sum();

        EventFullDto dto = EventsMap.eventFullDtoFromEvent(event);
        dto.setViews(Math.toIntExact(views));
        dto.setConfirmedRequests(0);

        return dto;
    }

    private Map<String, Long> getEventViews(List<Event> events) {
        if (events.isEmpty()) {
            return Map.of();
        }

        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .toList();

        StatsRequestDto request = StatsRequestDto.builder()
                .start(LocalDateTime.of(2000, 1, 1, 0, 0))
                .end(LocalDateTime.now().plusSeconds(1))
                .uris(uris)
                .unique(true)
                .build();

        return statsClient.getStats(request).stream()
                .collect(Collectors.toMap(
                        StatDto::getUri,
                        StatDto::getHits,
                        Long::sum
                ));
    }

    private void recordHit(String uri, String ip) {
        HitDto hit = new HitDto();
        hit.setApp("ewm-main-service");
        hit.setUri(uri);
        hit.setIp(ip);
        hit.setTimestamp(LocalDateTime.now());

        statsClient.hit(hit);
    }

    /** Метод проверяет идентификатор и существование пользователя
     * @param userId - идентификатор пользователя
     * @return объект пользователя
     */
    private User userCheck(Long userId) {
        EwmUtils.idIsNullCheck(userId,"Идентификатор пользователя userId");
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден "));
    }

    /** Метод проверяет корректность указания лимита участников
     * @param limit - лимит
     */
    private static void checkNegativeLimit(Integer limit) {
        if (limit != null && limit < 0) {
            throw new ValidationException("Лимит участников не может быть отрицательным");
        }
    }

    /** Метод проверяет идентификатор и существование события
     * @param eventId - идентификатор события
     * @return - объект события
     */
    private Event eventCheck(Long eventId) {
        EwmUtils.idIsNullCheck(eventId,"Идентификатор события eventId");
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено "));
    }
}
