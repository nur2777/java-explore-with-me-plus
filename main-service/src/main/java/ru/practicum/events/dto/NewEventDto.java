package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.model.Location;

import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.DATE_TIME_PATTERN;

/**
 * DTO для нового события
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    /**
     * Краткое описание события
     */
    @NotNull(message = "Краткое описание события не может быть пустым")
    @NotBlank(message = "Краткое описание события не может быть пустым")
    @Size(min = 20, message = "Краткое описание события должно быть не менее 20 символов")
    @Size(max = 2000, message = "Краткое описание события должно быть не менее 20 символов")
    private String annotation;

    /**
     * Id категории к которой относится событие
     */
    @NotNull(message = "Id категории события не может быть пустым")
    private Long category;

    /**
     * Полное описание события
     */
    @NotNull(message = "Полное описание события не может быть пустым")
    @NotBlank(message = "Полное описание события не может быть пустым")
    @Size(min = 20, message = "Полное описание события должно быть не менее 20 символов")
    @Size(max = 7000, message = "Полное описание события должно быть не менее 20 символов")
    private String description;

    /**
     * Дата и время на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
     */
    @NotNull(message = "Дата и время события не могут быть пустыми")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    /**
     * Широта и долгота места проведения события
     */
    @NotNull(message = "Координаты события не могут быть пустыми")
    private Location location;

    /**
     * Нужно ли оплачивать участие в событии
     */
    @Builder.Default
    private Boolean paid = false;

    /**
     * Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
     */
    @Builder.Default
    private Integer participantLimit = 0;

    /**
     * Нужна ли пре-модерация заявок на участие.
     * Если true, то все заявки будут ожидать подтверждения инициатором события.
     * Если false - то будут подтверждаться автоматически.
     */
    @Builder.Default
    private Boolean requestModeration = true;

    /**
     * Заголовок события
     */
    @NotNull(message = "Заголовок события не может быть пустым")
    @NotBlank(message = "Заголовок события не может быть пустым")
    @Size(min = 3, message = "Заголовок события должно быть не менее 20 символов")
    @Size(max = 120, message = "Заголовок события должно быть не менее 20 символов")
    private String title;
}
