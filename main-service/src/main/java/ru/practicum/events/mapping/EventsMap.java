package ru.practicum.events.mapping;

import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.Category;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.State;
import ru.practicum.events.model.Event;
import ru.practicum.users.dto.UserShortDTO;

public class EventsMap {

    public static Event newEventDtoToEvent(NewEventDto newEventDto) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        Category category = new Category();
        category.setId(newEventDto.getCategory());
        event.setCategory(category);
        event.setDescription(newEventDto.getDescription());
        event.setTitle(newEventDto.getTitle());
        event.setEventDate(newEventDto.getEventDate());
        event.setLocation(newEventDto.getLocation());
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        return event;
    }

    public static EventFullDto eventFullDtoFromEvent(Event event) {

        CategoryDto categoryDto = new CategoryDto(event.getCategory().getId(),event.getCategory().getName());
        UserShortDTO userShortDTO = new UserShortDTO(event.getInitiator().getId(),event.getInitiator().getName());
        State state = State.valueOf(event.getState());
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(userShortDTO)
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(state)
                .title(event.getTitle())
                .build();
    }
}
