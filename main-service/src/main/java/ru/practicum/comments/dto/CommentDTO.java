package ru.practicum.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для данных о комментарии
 */
@Data
@Builder
public class CommentDTO {
    /**
     * Идентификатор комментария
     */
    private Long id;

    /**
     * Идентификатор события
     */
    private Long eventId;

    /**
     * Идентификатор автора
     */
    private Long userId;

    /**
     * Текст комментария
     */
    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(max = 1000, message = "Комментарий должен быть максимум 1000 символов")
    @Size(min = 1, message = "Длина комментария должна быть минимум 1 символ")
    private String text;

    /**
     * Дата создания комментария
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime creationDate;

}
