package ru.practicum.users.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

/**
 * DTO для данных пользователя
 */
@Data
@Builder
public class UserDTO {
    /**
     * Идентификатор пользователя
     */
    private Long id;

    /**
     * Имя пользователя
     */
    @NotNull(message = "Имя пользователя не может быть пустым")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(max = 250, message = "Длина имени пользователя должна быть максимум 250 символов")
    @Size(min = 2, message = "Длина имени пользователя должна быть минимум 2 символа")
    private String name;

    /**
     * Электронная почта пользователя
     */
    @NotNull(message = "E-mail не может быть пустым")
    @NotBlank(message = "E-mail не может быть пустым")
    @Size(max = 254, message = "Длина имени пользователя должна быть максимум 250 символов")
    @Size(min = 6, message = "Длина имени пользователя должна быть минимум 2 символа")
    @Email(message = "Некорректный email")
    @Pattern(
            regexp = "^[^@]{1,64}@.*$",
            message = "Локальная часть email не должна превышать 64 символа"
    )
    private String email;
}
