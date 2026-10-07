package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.events.model.Location;
import ru.practicum.users.dto.UserShortDTO;

import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.DATE_TIME_PATTERN;

/**
 * DTO для возврата данных о событии
 */
@Data
@Builder
public class EventFullDto {

    /**
     * Идентификатор
     */
    private Long id;

    /**
     * Краткое описание события
     */
    private String annotation;

    /**
     * Категория
     */
    private CategoryDto category;

    /**
     * Количество одобренных заявок на участие в данном событии
     */
    private Integer confirmedRequests;

    /**
     * Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdOn;

    /**
     * Полное описание события
     */
    private String description;

    /**
     * Дата и время на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    /**
     * Пользователь (краткая информация)
     */
    private UserShortDTO initiator;

    /**
     * Широта и долгота места проведения события
     */
    @NotNull(message = "Координаты события не могут быть пустыми")
    private Location location;

    /**
     * Нужно ли оплачивать участие в событии
     */
    private Boolean paid;

    /**
     * Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
     */
    @Builder.Default
    private Integer participantLimit = 0;

    /**
     * Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime publishedOn;

    /**
     * Нужна ли пре-модерация заявок на участие.
     */
    @Builder.Default
    private Boolean requestModeration = true;

    /**
     * Состояние жизненного цикла события
     */
    private State state;

    /**
     * Заголовок события
     */
    private String title;

    /**
     * Количество просмотров события
     */
    private Integer views;
}
