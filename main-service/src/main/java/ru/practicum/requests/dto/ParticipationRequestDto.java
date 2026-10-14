package ru.practicum.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для заявки на участие
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    /**
     * Идентификатор заявки
     */
    private Long id;

    /**
     * Идентификатор события
     */
    private Long event;

    /**
     * Идентификатор пользователя, отправившего заявку
     */
    private Long requester;

    /**
     * Статус заявки
     */
    private String status;

    /**
     * Дата и время создания заявки (в формате "yyyy-MM-dd HH:mm:ss")
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime created;
}
