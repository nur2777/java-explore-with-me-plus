package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.users.dto.UserShortDTO;

import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.DATE_TIME_PATTERN;

/**
 * DTO для краткой информации о событии
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {

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
     * Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    /**
     * Пользователь (краткая информация)
     */
    private UserShortDTO initiator;

    /**
     * Нужно ли оплачивать участие в событии
     */
    private Boolean paid;

    /**
     * Заголовок события
     */
    private String title;

    /**
     * Количество просмотров события
     */
    private Integer views;
}
