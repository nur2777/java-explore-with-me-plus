package ru.practicum.categories.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для данных категории
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    /**
     * Идентификатор категории
     */
    private Long id;

    /**
     * Имя категории
     */
    @NotBlank(message = "Имя категории не может быть пустым")
    @Size(min = 1, max = 50, message = "Длина имени категории должна быть от 1 до 50 символов")
    private String name;
}
