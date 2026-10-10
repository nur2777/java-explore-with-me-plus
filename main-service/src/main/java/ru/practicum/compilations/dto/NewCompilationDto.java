package ru.practicum.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {

    @UniqueElements(message = "Список events должен содержать уникальные значения")
    private List<Long> events;

    /**
     * Закреплена ли подборка на главной странице сайта
     */
    @Builder.Default
    private Boolean pinned = false;

    /**
     * Заголовок подборки
     */
    @NotNull(message = "Заголовок подборки не может быть пустым")
    @NotBlank(message = "Заголовок подборки не может быть пустым")
    @Size(min = 1, message = "Заголовок подборки должен быть не менее 1 символа")
    @Size(max = 50, message = "Заголовок подборки должен быть не более 50 символов")
    private String title;
}
