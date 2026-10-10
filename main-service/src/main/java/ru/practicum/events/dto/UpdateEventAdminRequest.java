package ru.practicum.events.dto;

import lombok.Data;
import ru.practicum.events.model.Location;

import java.time.LocalDateTime;

@Data
public class UpdateEventAdminRequest {

    private String annotation;
    private Long category;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    private String title;
}