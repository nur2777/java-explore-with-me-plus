package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.events.model.Location;

import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.DATE_TIME_PATTERN;

@Data
public class UpdateEventAdminRequest {

    @Size(min = 20, message = "Краткое описание события должно быть не менее 20 символов")
    @Size(max = 2000, message = "Краткое описание события должно быть не менее 20 символов")
    private String annotation;

    private Long category;

    @Size(min = 20, message = "Полное описание события должно быть не менее 20 символов")
    @Size(max = 7000, message = "Полное описание события должно быть не менее 20 символов")
    private String description;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;

    @Size(min = 3, message = "Заголовок события должно быть не менее 20 символов")
    @Size(max = 120, message = "Заголовок события должно быть не менее 20 символов")
    private String title;
}