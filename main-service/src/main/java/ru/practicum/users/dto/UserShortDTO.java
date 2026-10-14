package ru.practicum.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO для коротких данных пользователя
 */
@Data
@Builder
@AllArgsConstructor
public class UserShortDTO {

    /**
     * Идентификатор пользователя
     */
    private Long id;
    /**
     * Имя пользователя
     */
    private String name;
}
