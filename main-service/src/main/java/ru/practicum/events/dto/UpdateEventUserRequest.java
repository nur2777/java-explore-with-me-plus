package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.model.Location;

import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.DATE_TIME_PATTERN;

/**
 * DTO для изменения данных о событии
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest {

    /**
     * Новое описание события
     */
    @Size(min = 20, message = "Краткое описание события должно быть не менее 20 символов")
    @Size(max = 2000, message = "Краткое описание события должно быть не более 2000 символов")
    private String annotation;

    /**
     * Id категории к которой относится событие
     */
    private Long category;

    /**
     * Полное описание события
     */
    @Size(min = 20, message = "Полное описание события должно быть не менее 20 символов")
    @Size(max = 7000, message = "Полное описание события должно быть не более 7000 символов")
    private String description;

    /**
     * Дата и время на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    /**
     * Широта и долгота места проведения события
     */
    private Location location;

    /**
     * Нужно ли оплачивать участие в событии
     */
    private Boolean paid;

    /**
     * Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
     */
    private Integer participantLimit;

    /**
     * Нужна ли пре-модерация заявок на участие.
     */
    private Boolean requestModeration;

    /**
     * Изменение состояния события
     */
    private StateAction stateAction;

    /**
     * Заголовок события
     */
    @Size(min = 3, message = "Заголовок события должно быть не менее 3 символов")
    @Size(max = 120, message = "Заголовок события должно быть не более 120 символов")
    private String title;
}
